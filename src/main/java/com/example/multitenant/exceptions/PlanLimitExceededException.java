package com.example.multitenant.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlanLimitExceededException extends RuntimeException {
    public PlanLimitExceededException(String message) {
        super(message);
        log.error("[Plant Limit Exceeded Exception] message: {}", message);
    }

    // TODO: constructor with organizationId + plan + what they exceeded
    // public PlanLimitExceededException(String message, Integer organizationId) {
    //     super(message);
    //     log.error("[Plant Limit Exceeded Exception] message: {}", message);
    // }
}
