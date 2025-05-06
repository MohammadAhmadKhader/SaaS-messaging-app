package com.example.multitenant.services.stripe;

import java.util.concurrent.Flow.Subscription;

import org.springframework.stereotype.Service;

import com.example.multitenant.config.StripeConfig;
import com.example.multitenant.models.InternalStripeSubscription;
import com.example.multitenant.models.enums.StripePlan;
import com.example.multitenant.repository.InternalStripeCustomersRepository;
import com.example.multitenant.repository.InternalStripeSubscriptionsRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripeHelperService {
    private final StripeConfig stripeConfig;

    public Customer getCustomerById(String customerId) throws StripeException {
        return Customer.retrieve(customerId);
    }

    public String getStripeProductIdByPriceId(String priceId) {
        if(priceId.equals(this.stripeConfig.getPriceStarterId())) {
            return this.stripeConfig.getStarterPlanId();

        } else if(priceId.equals(this.stripeConfig.getPriceProId())) {
            return this.stripeConfig.getProPlanId();

        } else if(priceId.equals(this.stripeConfig.getPriceEnterpriseId())) {
            return this.stripeConfig.getEnterprisePlanId();
        }

        return null;
    }

    public String getStripeProductDisplayName(String priceId) {
        if(priceId.equals(this.stripeConfig.getPriceStarterId())) {
            return StripePlan.STARTER.toString();

        } else if(priceId.equals(this.stripeConfig.getPriceProId())) {
            return StripePlan.PRO.toString();

        } else if(priceId.equals(this.stripeConfig.getPriceEnterpriseId())) {
            return StripePlan.ENTERPRISE.toString();
        }

        return null;
    }
}