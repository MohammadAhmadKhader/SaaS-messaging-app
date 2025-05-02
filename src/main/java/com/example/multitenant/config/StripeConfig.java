package com.example.multitenant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.example.multitenant.exceptions.AppStripeException;
import com.example.multitenant.models.enums.StripeMode;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Component
public class StripeConfig {
    @Value("${stripe.secret}")
    private String secret;
    
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.tiers.starter.price.id}")
    private String priceStarterId;

    @Value("${stripe.tiers.pro.price.id}")
    private String priceProId;

    @Value("${stripe.tiers.enterprise.id}")
    private String enterprisePlanId;

    @Value("${stripe.tiers.starter.id}")
    private String starterPlanId;

    @Value("${stripe.tiers.pro.id}")
    private String proPlanId;

    @Value("${stripe.tiers.enterprise.price.id}")
    private String priceEnterpriseId;

    @Value("${stripe.checkout.success_url}")
    private String successUrl;

    @Value("${stripe.checkout.cancel_url}")
    private String cancelUrl;

    @Value("${stripe.mode}")
    private StripeMode stripeMode;

    public boolean isValidPriceId(String priceId) {
        return this.getPriceStarterId().equals(priceId) || 
        this.getPriceProId().equals(priceId) || 
        this.getPriceEnterpriseId().equals(priceId);
    }

    @PostConstruct
    public void validateKeys() {
        if (this.getSecret() == null || this.getSecret().isBlank()) {
            var errMsg = "[Stripe Error] missing stripe secret key";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getWebhookSecret() == null || this.getWebhookSecret().isBlank()) {
            var errMsg = "missing webhook secret";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getPriceStarterId() == null || this.getPriceStarterId().isBlank()) {
            var errMsg = "missing price starter id";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getPriceProId() == null || this.getPriceProId().isBlank()) {
            var errMsg = "missing price pro id";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getPriceEnterpriseId() == null || this.getPriceEnterpriseId().isBlank()) {
            var errMsg = "missing price enterprise id";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getSuccessUrl() == null || this.getSuccessUrl().isBlank()) {
            var errMsg = "missing success url";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getCancelUrl() == null || this.getCancelUrl().isBlank()) {
            var errMsg = "missing cancel url";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }
        
        if (this.getStarterPlanId() == null || this.getStarterPlanId().isBlank()) {
            var errMsg = "missing starter plan id";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getProPlanId() == null || this.getProPlanId().isBlank()) {
            var errMsg = "missing pro plan id";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }

        if (this.getEnterprisePlanId() == null || this.getEnterprisePlanId().isBlank()) {
            var errMsg = "missing enterprise plan id";
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }
    }
}