package com.example.multitenant.services.stripe;

import org.springframework.stereotype.Service;

import com.example.multitenant.config.StripeConfig;
import com.example.multitenant.exceptions.AppStripeException;
import com.example.multitenant.models.enums.StripeEventType;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripeHandlerService {
    private final StripeService stripeService;
    private final StripeHelperService stripeHelperService;

    public void handleCheckoutSessionCompletedEvent(Event event) {
        event.getDataObjectDeserializer().getObject().ifPresent((obj) ->{ 
            var session = (Session) obj;

            Subscription sub;
            try {
                sub = this.stripeService.getSubscriptionById(session.getSubscription());
            } catch (StripeException ex) {
                var errMsg = String.format(
                "an error has occured during attempt to fetch subsecription with id '%s'",
                session.getSubscription());
                throw new AppStripeException(errMsg, ex);
            }
                
            if(sub == null) {
                var errMsg = String.format(
                "subscription with id '%s' was not found", 
                session.getSubscription());
                throw new AppStripeException(errMsg);
            }

            var userId = session.getMetadata().get("user_id");
            if(userId == null || userId.isBlank()) {
                var errMsg = String.format(
                "user id was not found in metadata on stripe session id '%s'", 
                session.getSubscription());
                throw new AppStripeException(errMsg);
            }

            var organizationId = session.getMetadata().get("organization_id");
            if(organizationId == null || organizationId.isBlank()) {
                var errMsg = String.format(
                "organization id was not found in metadata on stripe session id '%s'",
                session.getSubscription());
                throw new AppStripeException(errMsg);
            }

            var userIdAsLong = Long.valueOf(userId);
            var organizationIdAsInteger = Integer.valueOf(organizationId);
                    
            var itemsCount = 0;
            var priceId = "";
            for (var item : sub.getItems().autoPagingIterable()) {
                var price = item.getPrice();
                priceId = price.getId();
                itemsCount++;
            }
            var productId = this.stripeHelperService.getStripeProductIdByPriceId(priceId);
            if(productId == null) {
                var errMsg = String.format("product was not found for price id '%s'", priceId);
                throw new AppStripeException(errMsg);
            }

            if(itemsCount == 0) {
                var errMsg = String.format(
                "no subsecription items were found for subscription with id '%s'", 
                session.getSubscription());
                throw new AppStripeException(errMsg);
            }

            if(itemsCount > 1) {
                var errMsg = String.format("items are supposed to be only one which" +
                "is the plan selected, but received more than one, subsecription.getItems().getData() = %s",
                 sub.getItems().getData());
                throw new AppStripeException(errMsg);
            }

            if(priceId.equals("")) {
                throw new AppStripeException("priceId is missing");
            }

            if(productId.equals("")) {
                throw new AppStripeException("product id is missing");
            }

            Subscription stripeSub = null;
            try {
                stripeSub = this.stripeService.getSubscriptionById(session.getSubscription());
            } catch (StripeException ex) {
                var errMsg = String.format(
                "an error occured during attempt to fetch subsecription from stripe api", 
                session.getSubscription());
                throw new AppStripeException(errMsg, ex);
            }

            if(stripeSub == null) {
                var errMsg = String.format(
                "stripe subsecription with session id '%s' was received as null after fetching from stripe api", 
                session.getSubscription());
                throw new AppStripeException(errMsg);
            }

            try {
                this.stripeService.createInternalSubscription(session, stripeSub, organizationIdAsInteger, userIdAsLong);
            } catch (StripeException ex) {
                var errMsg = String.format(
                "an error occured during attempt to created an internal subsecription with stripe session id '%s'", 
                session.getSubscription());
                throw new AppStripeException(errMsg, ex);
            }
        });
    }

    public void handleSubscriptionUpdate(Event event){ 
        var deserializer = event.getDataObjectDeserializer();
        var stripeObject = deserializer.getObject();
        stripeObject.ifPresent((obj) -> {
            var subsecription = (Subscription) obj;
            if (subsecription.getStatus().equals("canceled")) {
                log.info("subscription {} was canceled, skipping update method");
                return;
            }
            log.info("received updated sub: {}", obj.toJson());

            var internalSub = this.stripeService.updateSubsecription(subsecription);
            if(internalSub == null) {
                var errMsg = String.format("internal subsecription with id '%s' was not found", subsecription.getId());
                log.error(errMsg);
                throw new AppStripeException(errMsg);
            }
            
        });
    }

    // customers are not allowed to cancel, but this will handle the auto cancellation when its time is over
    public void handleSubscriptionCancellation(Event event){ 
        var deserializer = event.getDataObjectDeserializer();
        var stripeObject = deserializer.getObject();
        stripeObject.ifPresent((obj) -> {
            var subsecription = (Subscription) obj;
            log.info("received cancelled sub: {}", subsecription.toJson());

            var subId = subsecription.getId();
            var internalSub = this.stripeService.cancelSubsecription(subId);
            if(internalSub == null) {
                var errMsg = String.format("internal subsecription with id '%s' was not found", subId);
                log.error(errMsg);
                throw new AppStripeException(errMsg);
            }

        });
    }
}