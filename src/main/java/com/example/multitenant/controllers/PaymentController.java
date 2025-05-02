package com.example.multitenant.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.services.stripe.StripeService;
import com.example.multitenant.utils.AppUtils;
import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j 
@RestController
@RequiredArgsConstructor()
@RequestMapping("/api/payment")
public class PaymentController {
    private final StripeService stripeService;

    @PostMapping("")
    public ResponseEntity<Object> createCheckoutSession(@RequestBody Map<String, String> reqBody) throws StripeException {
        var tenantId = AppUtils.getTenantId();
        var user = AppUtils.getUserFromAuth();

        var priceId = reqBody.get("priceId");
        if(priceId == null || priceId.isBlank()) {
            return ResponseEntity.internalServerError().body(ApiResponses.StripeError("priceId is missing"));
        }

        var checkoutSesion = this.stripeService.createCheckoutSession(user, priceId, tenantId);
        var body = ApiResponses.OneKey("sessionUrl", checkoutSesion.getUrl());

        return ResponseEntity.ok().body(body);
    }
    
}