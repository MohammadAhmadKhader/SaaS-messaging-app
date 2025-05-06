package com.example.multitenant.services.stripe;

import java.time.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.config.StripeConfig;
import com.example.multitenant.exceptions.AppStripeException;
import com.example.multitenant.models.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.repository.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;

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
    private final StripeHelperService stripeHelperService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfig.getSecret();
    }

    // frontend will hit backend on this
    // this method will ensure we always have customer before checking out and its set to the checkout session
    public Session createCheckoutSession(User user, String priceId, Integer tenantId) throws StripeException {
        if(this.internalStripeSubscriptionsRepository.hasActive(tenantId)) {
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

        var localCustomer = new InternalStripeCustomer();
        localCustomer.setUser(user);
        localCustomer.setStripeCustomerId(stripeCustomer.getId());
        this.internalStripeCustomersRepository.save(localCustomer);

        return localCustomer;
    }

    public InternalStripeSubscription createInternalSubscription(Session session, Subscription stripeSub, Integer organizationId, Long userId) throws StripeException {
        var intenralCustomer = this.internalStripeCustomersRepository.findCustomerByUserId(userId);
        if(intenralCustomer == null) {
            var errMsg = String.format(
            "internal customer for user with id '%s' is null during chechkout session that was completed successfully",
            userId);
            throw new AppStripeException(errMsg);
        }

        PaymentMethod paymentMethod = null;
        if (stripeSub.getDefaultPaymentMethodObject() != null) {
            paymentMethod = stripeSub.getDefaultPaymentMethodObject();
            var paymentMethodType = paymentMethod.getType();

            if (!paymentMethodType.equals("card")) {
                log.error("invalid payment method, received {}, payment_method: {}",paymentMethodType ,paymentMethod.toJson());
                throw new AppStripeException("invalid payment method");
            }

        } else {
            log.error("payment method is required received payemnt as null, checkout_session: {}", stripeSub.toJson());
            throw new AppStripeException("payment method is required");
        }

        var item = stripeSub.getItems().getData().get(0);
        var cancelAt = stripeSub.getCancelAt() == null ? null : Instant.ofEpochMilli(stripeSub.getCancelAt());
        var country = session.getCustomerDetails().getAddress().getCountry();

        // price
        var price = item.getPrice();
        var stripeProductId = price.getProduct();
        var currency = price.getCurrency();
        var amount = price.getUnitAmount();
        var status = stripeSub.getStatus();
        var interval = price.getRecurring().getInterval();

        // card
        var card = paymentMethod.getCard();
        var brand = card.getBrand();
        var last4 = card.getLast4();

        // periods
        var currPerioudEnd = Instant.ofEpochSecond(item.getCurrentPeriodEnd());
        var currPerioudStart = Instant.ofEpochSecond(item.getCurrentPeriodStart());

        // ids
        var stripeCustomerId = stripeSub.getCustomer();
        var priceId = price.getId();
        var stripeSubscriptionId = stripeSub.getId();
        var productDisplayName = this.stripeHelperService.getStripeProductDisplayName(priceId);
        var internalCustomerId = intenralCustomer.getId();
        
        var internalSub = InternalStripeSubscription.builder()
                //id's
                .stripeSubscriptionId(stripeSubscriptionId)
                .stripeCustomerId(stripeCustomerId)
                .stripePriceId(priceId)
                .userId(userId)
                .stripeProductId(stripeProductId)
                .internalCustomerId(internalCustomerId)
                .organizationId(organizationId)
                // periods
                .currentPeriodStart(currPerioudStart)
                .currentPeriodEnd(currPerioudEnd)
                .cancelPeriodAt(cancelAt)
                 // card
                .brand(brand)
                .last4(last4)
                // others
                .stripeProductName(productDisplayName)
                .country(country)
                .amount(amount)
                .interval(interval)
                .currency(currency)
                .status(status)
                .build();

        return this.internalStripeSubscriptionsRepository.save(internalSub);
    }

    public InternalStripeSubscription cancelSubsecription(String stripeSubId) {
        var internalSub = this.internalStripeSubscriptionsRepository.findByStripeId(stripeSubId);
        if (internalSub == null) {
            return null;
        }

        internalSub.setStatus("inactive");
        internalSub.setCancelPeriodAt(Instant.now());
        return this.internalStripeSubscriptionsRepository.save(internalSub);
    }

    public InternalStripeSubscription updateSubsecription(Subscription subscription) {
        var internalSub = this.internalStripeSubscriptionsRepository.findByStripeId(subscription.getId());
        if (internalSub == null) {
            return null;
        }

        var item = subscription.getItems().getData().get(0);
        var price = item.getPrice();
        var amount = price.getUnitAmount();
        var status = subscription.getStatus();
        var interval = price.getRecurring().getInterval();

        internalSub.setStatus(status);
        internalSub.setAmount(amount);
        internalSub.setInterval(interval);
        internalSub.setStripeProductName(this.stripeHelperService.getStripeProductDisplayName(price.getId()));

        return this.internalStripeSubscriptionsRepository.save(internalSub);
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

    public SubscriptionCollection getUserSubscriptions(String stripeCustomerId) throws StripeException {
        var params = new HashMap<String, Object>();
        params.put("customer", stripeCustomerId);
        return Subscription.list(params);
    }

    public Subscription getSubscriptionById(String subscriptionId, String... expands) throws StripeException {
        var params = SubscriptionRetrieveParams.builder()
        .addExpand("default_payment_method")
        .addAllExpand(List.of(expands))
        .build();

        return Subscription.retrieve(subscriptionId, params, null);
    }

    public InternalStripeSubscription getOrgActiveSubsecription(Integer orgId) {
        return this.internalStripeSubscriptionsRepository.findActiveByOrgId(orgId);
    }
}