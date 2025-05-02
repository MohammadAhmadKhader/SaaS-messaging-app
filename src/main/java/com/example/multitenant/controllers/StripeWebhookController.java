package com.example.multitenant.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.multitenant.config.StripeConfig;
import com.example.multitenant.models.enums.StripeEventType;
import com.example.multitenant.services.stripe.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {
    private final StripeConfig stripeConfig;
    private final StripeService stripeService;

    @PostMapping("")
    public ResponseEntity<Object> handleStripeEvent(@RequestBody byte[] payloadBytes, @RequestHeader("stripe-signature") String sigHeader) throws SignatureVerificationException, IOException {
        var payloadString = new String(payloadBytes, StandardCharsets.UTF_8);
        var event = Webhook.constructEvent(payloadString, sigHeader, stripeConfig.getWebhookSecret());
        log.info("received event: {}", event.getType());
        if(event.getType().equals(StripeEventType.CHECKOUT_SESSION_COMPLETED.getEvent())) {
            this.stripeService.handleCheckoutSessionCompletedEvent(event);
        }

        return ResponseEntity.ok().build();
    }
}
