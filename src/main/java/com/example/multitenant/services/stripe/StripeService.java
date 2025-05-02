package com.example.multitenant.services.stripe;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.config.StripeConfig;
import com.example.multitenant.exceptions.AppStripeException;
import com.example.multitenant.models.InternalStripeCustomer;
import com.example.multitenant.models.InternalStripeSubscription;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.StripeEventType;
import com.example.multitenant.repository.InternalStripeCustomersRepository;
import com.example.multitenant.repository.InternalStripeSubscriptionsRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.SubscriptionItem;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.SubscriptionData;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripeService {
    private final InternalStripeCustomersRepository internalStripeCustomersRepository;
    private final InternalStripeSubscriptionsRepository internalStripeSubscriptionsRepository;
    private final StripeConfig stripeConfig;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfig.getSecret();
    }

    // frontend will hit backend on this
    // this method will ensure we always have customer before checking out and its set to the checkout session
    public Session createCheckoutSession(User user, String priceId, Integer tenantId) throws StripeException {
        if(this.internalStripeSubscriptionsRepository.hasValidActiveSubscription(tenantId)) {
            throw new AppStripeException("organization has already an active subscription");
        }

        var metadata = new HashMap<String, String>();
        metadata.put("organization_id", tenantId.toString());
        metadata.put("user_id", Long.valueOf(user.getId()).toString());

        var isValidPriceId = this.stripeConfig.isValidPriceId(priceId);
        if(!isValidPriceId) {
            throw new AppStripeException(String.format("invalid priceId received: %s", priceId));
        }

        var stripeCustomer = this.internalStripeCustomersRepository.findCustomerByUserId(user.getId());
        if(stripeCustomer == null) {
            stripeCustomer = this.createStripeCustomer(user);
        }

        // we only allowed to specify the email or customer id but not both
        var params = SessionCreateParams.builder()
            .setCustomer(stripeCustomer.getStripeCustomerId())
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                            .setPrice(priceId)
                            .setQuantity(1L)
                            .build())
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setSuccessUrl(stripeConfig.getSuccessUrl())
            .setCancelUrl(stripeConfig.getCancelUrl())
            .putAllMetadata(metadata)
            .build();

        return Session.create(params);
    }

    public InternalStripeCustomer createStripeCustomer(User user) throws StripeException {
        var metadata = new HashMap<String, String>();
        metadata.put("user_id", String.valueOf(user.getId()));

        var customerParams = CustomerCreateParams.builder()
                .setName(user.getFullName())
                .setEmail(user.getEmail())
                .setMetadata(metadata)
                .build();

        var stripeCustomer = Customer.create(customerParams);

        // customer in our db
        var localCustomer = new InternalStripeCustomer();
        localCustomer.setUser(user);
        localCustomer.setStripeCustomerId(stripeCustomer.getId());
        this.internalStripeCustomersRepository.save(localCustomer);

        return localCustomer;
    }

    public InternalStripeSubscription createInternalSubscription(Subscription stripeSub, Integer organizationId, Long userId) throws StripeException {
        var intenralCustomer = this.internalStripeCustomersRepository.findCustomerByUserId(userId);
        if(intenralCustomer == null) {
            throw new AppStripeException(String.format("""
                internal customer for user with id '%s' is null during chechkout session that was completed successfully
            """.trim(), userId));
        }

        var item = stripeSub.getItems().getData().get(0);
        var cancelAt = stripeSub.getCancelAt() == null ? null : Instant.ofEpochMilli(stripeSub.getCancelAt());
        var internalSub = InternalStripeSubscription.builder()
                .stripeSubscriptionId(stripeSub.getId())
                .stripeCustomerId(stripeSub.getCustomer())
                .stripePriceId(item.getPrice().getId())
                .userId(userId)
                .tier(item.getPrice().getProduct())
                .internalCustomerId(intenralCustomer.getId())
                .organizationId(organizationId)
                .currentPeriodStart(Instant.ofEpochSecond(item.getCurrentPeriodStart()))
                .currentPeriodEnd(Instant.ofEpochSecond(item.getCurrentPeriodEnd()))
                .cancelPeriodAt(cancelAt)
                .status(stripeSub.getStatus())
                .build();

        this.internalStripeSubscriptionsRepository.save(internalSub);

        return internalSub;
    }

    public Subscription getSubscriptionByOrganizationId(String stripeCustomerId, String organizationId) throws StripeException {
        var params = new HashMap<String, Object>();
        params.put("customer", stripeCustomerId);
    
        var subscriptions = Subscription.list(params);
    
        for (var subscription : subscriptions.getData()) {
            if (organizationId.equals(subscription.getMetadata().get("organization_id"))) {
                return subscription;
            }
        }
    
        return null;
    }

    public void handleCheckoutSessionCompletedEvent(Event event) {
        event.getDataObjectDeserializer().getObject().ifPresent((obj) ->{ 
                if(obj instanceof Session) {
                    var session = (Session) obj;
                    log.info("subscription id: {}", session.getId());
                    log.info("customer id: {}", session.getCustomer());

                    Subscription sub;
                    try {
                        sub = this.getSubscriptionById(session.getSubscription());
                    } catch (StripeException ex) {
                        throw new AppStripeException(String.format("an error has occured during attempt to fetch subsecription with id '%s'", session.getSubscription()), ex);
                    }
                    
                    if(sub == null) {
                        throw new AppStripeException(String.format("subscription with id '%s' was not found", session.getSubscription()));
                    }

                    var userId = session.getMetadata().get("user_id");
                    if(userId == null || userId.isBlank()) {
                        throw new AppStripeException(String.format("user id was not found in metadata on stripe session id '%s'", session.getSubscription()));
                    }

                    var organizationId = session.getMetadata().get("organization_id");
                    if(organizationId == null || organizationId.isBlank()) {
                        throw new AppStripeException(String.format("organization id was not found in metadata on stripe session id '%s'", session.getSubscription()));
                    }

                    var userIdAsLong = Long.valueOf(userId);
                    var organizationIdAsInteger = Integer.valueOf(organizationId);
                    
                    var iterations = 0;
                    var priceId = "";
                    for (var item : sub.getItems().autoPagingIterable()) {
                        var price = item.getPrice();
                        priceId = price.getId();
                        iterations++;
                    }
                    var tier = this.getPlanByPriceId(priceId);
                    if(tier == null) {
                        throw new AppStripeException(String.format("price tier was not found for price id '%s'", priceId));
                    }

                    log.info("received the following priceId {}", priceId);
                    log.info("received the following tier {}", tier);

                    if(iterations == 0) {
                        throw new AppStripeException(String.format("no subsecription items were found for subscription with id '%s'", session.getSubscription()));
                    }

                    if(iterations > 1) {
                        throw new AppStripeException(String.format("items are supposed to be only one which" +
                        "is the plan selected, but received more than one, subsecription.getItems().getData() = %s", sub.getItems().getData()));
                    }

                    if(priceId.equals("")) {
                        throw new AppStripeException("priceId is missing");
                    }

                    if(tier.equals("")) {
                        throw new AppStripeException("tier is missing");
                    }

                    Subscription stripeSub = null;
                    try {
                        stripeSub = this.getSubscriptionById(session.getSubscription());
                    } catch (StripeException ex) {
                        throw new AppStripeException(String.format("an error occured during attempt to fetch subsecription from stripe api", session.getSubscription()), ex);
                    }

                    if(stripeSub == null) {
                        throw new AppStripeException(String.format("stripe subsecription with session id '%s' was received as null after fetching from stripe api", session.getSubscription()));
                    }

                    try {
                        this.createInternalSubscription(stripeSub, organizationIdAsInteger, userIdAsLong);
                    } catch (StripeException ex) {
                        throw new AppStripeException(String.format("an error occured during attempt to created an internal subsecription with stripe session id '%s'", session.getSubscription()), ex);
                    }

                } else {
                    log.error("[Stripe Error] event type {} with object was expected to be instanceof {}", StripeEventType.CHECKOUT_SESSION_COMPLETED, Subscription.class);
                }
            });
    }

    public SubscriptionCollection getUserSubscriptions(String stripeCustomerId) throws StripeException {
        var params = new HashMap<String, Object>();
        params.put("customer", stripeCustomerId);
        return Subscription.list(params);
    }

    public Subscription getSubscriptionById(String subscriptionId) throws StripeException {
        return Subscription.retrieve(subscriptionId);
    }

    public Customer getCustomerById(String customerId) throws StripeException {
        return Customer.retrieve(customerId);
    }

    public String getPlanByPriceId(String priceId) {
        if(priceId.equals(this.stripeConfig.getPriceStarterId())) {
            return this.stripeConfig.getStarterPlanId();

        } else if(priceId.equals(this.stripeConfig.getPriceProId())) {
            return this.stripeConfig.getProPlanId();

        } else if(priceId.equals(this.stripeConfig.getPriceEnterpriseId())) {
            return this.stripeConfig.getEnterprisePlanId();
        }

        return null;
    }
}