package com.example.multitenant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.multitenant.exceptions.AppStripeException;
import com.example.multitenant.models.enums.StripeLimit;
import com.example.multitenant.models.enums.StripePlan;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class StripePlansConfig {

    // Free Tier
    @Value("${stripe.tiers.free.org-max-categories}")
    private Integer freeMaxCategories;

    @Value("${stripe.tiers.free.org-max-roles}")
    private Integer freeMaxRoles;

    @Value("${stripe.tiers.free.org-max-members}")
    private Integer freeMaxMembers;

    @Value("${stripe.tiers.free.org-max-category-channels}")
    private Integer freeMaxCategoryChannels;

    // Starter Tier
    @Value("${stripe.tiers.starter.org-max-categories}")
    private Integer starterMaxCategories;

    @Value("${stripe.tiers.starter.org-max-roles}")
    private Integer starterMaxRoles;

    @Value("${stripe.tiers.starter.org-max-members}")
    private Integer starterMaxMembers;

    @Value("${stripe.tiers.starter.org-max-category-channels}")
    private Integer starterMaxCategoryChannels;

    // Pro Tier
    @Value("${stripe.tiers.pro.org-max-categories}")
    private Integer proMaxCategories;

    @Value("${stripe.tiers.pro.org-max-roles}")
    private Integer proMaxRoles;

    @Value("${stripe.tiers.pro.org-max-members}")
    private Integer proMaxMembers;

    @Value("${stripe.tiers.pro.org-max-category-channels}")
    private Integer proMaxCategoryChannels;

    // Enterprise Tier
    @Value("${stripe.tiers.enterprise.org-max-categories}")
    private Integer enterpriseMaxCategories;

    @Value("${stripe.tiers.enterprise.org-max-roles}")
    private Integer enterpriseMaxRoles;

    @Value("${stripe.tiers.enterprise.org-max-members}")
    private Integer enterpriseMaxMembers;

    @Value("${stripe.tiers.enterprise.org-category-channels}")
    private Integer enterpriseMaxCategoryChannels;

    @PostConstruct
    public void validateConfig() {
        validate(starterMaxCategories, "starter org-max-categories");
        validate(starterMaxRoles, "starter org-max-roles");
        validate(starterMaxMembers, "starter org-max-members");
        validate(starterMaxCategoryChannels, "starter org-max-category-channels");

        validate(proMaxCategories, "pro org-max-categories");
        validate(proMaxRoles, "pro org-max-roles");
        validate(proMaxMembers, "pro org-max-members");
        validate(proMaxCategoryChannels, "pro org-max-category-channels");

        validate(enterpriseMaxCategories, "enterprise org-max-categories");
        validate(enterpriseMaxRoles, "enterprise org-max-roles");
        validate(enterpriseMaxMembers, "enterprise org-max-members");
        validate(enterpriseMaxCategoryChannels, "enterprise org-category-channels");

        validate(freeMaxCategories, "free org-max-categories");
        validate(freeMaxRoles, "free org-max-roles");
        validate(freeMaxMembers, "free org-max-members");
        validate(freeMaxCategoryChannels, "free org-max-category-channels");
    }

    public Integer getMaxAllowed(StripeLimit limit, StripePlan plan) {
        if (limit == null || plan == null) {
            throw new IllegalArgumentException("limit and plan must not be null");
        }
    
        return switch (limit) {
            case CATEGORIES -> switch (plan) {
                case STARTER -> this.getStarterMaxCategories();
                case PRO -> this.getProMaxCategories();
                case ENTERPRISE -> this.getEnterpriseMaxCategories();
                case FREE -> this.getFreeMaxCategories();
            };
            case CATEGORY_CHANNELS -> switch (plan) {
                case STARTER -> this.getStarterMaxCategoryChannels();
                case PRO -> this.getProMaxCategoryChannels();
                case ENTERPRISE -> this.getEnterpriseMaxCategoryChannels();
                case FREE -> this.getFreeMaxCategoryChannels();
            };
            case MEMBERS -> switch (plan) {
                case STARTER -> this.getStarterMaxMembers();
                case PRO -> this.getProMaxMembers();
                case ENTERPRISE -> this.getEnterpriseMaxMembers();
                case FREE -> this.getFreeMaxMembers();
            };
            case ROLES -> switch (plan) {
                case STARTER -> this.getStarterMaxRoles();
                case PRO -> this.getProMaxRoles();
                case ENTERPRISE -> this.getEnterpriseMaxRoles();
                case FREE -> this.getFreeMaxRoles();
            };
            default -> throw new IllegalArgumentException("unknown limit: " + limit);
        };
    }

    private void validate(Integer value, String configName) {
        if (value == null) {
            var errMsg = "missing stripe config value: " + configName;
            log.error(errMsg);
            throw new AppStripeException(errMsg);
        }
    }
}