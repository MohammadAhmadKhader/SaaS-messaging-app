package com.example.multitenant.interceptors;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class LoggingInterceptor implements HandlerInterceptor {
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(BLUE + "Incoming request:" + RESET + " {} {}", GREEN + request.getMethod(), request.getRequestURI() + RESET);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        if (req.getRequestURI().startsWith("/ws")) {
            return;
        }

        if (req instanceof ContentCachingRequestWrapper reqWrapper &&
            res instanceof ContentCachingResponseWrapper resWrapper) {

            var reqBody = new String(reqWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            var resBody = new String(resWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            var status = resWrapper.getStatus();

            String statusColor;
            if (status >= 200 && status < 300) {
                statusColor = GREEN;
            } else if (status >= 400 && status < 500) {
                statusColor = YELLOW;
            } else if (status >= 500) {
                statusColor = RED;
            } else {
                statusColor = BLUE;
            }

            log.info(CYAN + "Request body:" + " {}", reqBody + RESET);
            // TODO: implement redacting for the response bodt
            log.info(YELLOW + "Response body:" + RESET + " {}", resBody);
            log.info(statusColor + "Response status:" + " {}", status + RESET);
        } else {
            log.warn("Request or Response is not wrapped properly. Skipping body logging.");
        }
    }
}