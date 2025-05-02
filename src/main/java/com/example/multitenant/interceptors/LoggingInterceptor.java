package com.example.multitenant.interceptors;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.example.multitenant.config.StripeConfig;
import com.example.multitenant.models.enums.StripeMode;
import com.example.multitenant.utils.ConsoleColorUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class LoggingInterceptor implements HandlerInterceptor {
    private final StripeConfig stripeConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(ConsoleColorUtils.blue("Incoming request:") + " {} {}", ConsoleColorUtils.greenNoReset(request.getMethod()), ConsoleColorUtils.reset(request.getRequestURI()));
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
                statusColor = ConsoleColorUtils.GREEN;
            } else if (status >= 400 && status < 500) {
                statusColor = ConsoleColorUtils.YELLOW;
            } else if (status >= 500) {
                statusColor = ConsoleColorUtils.RED;
            } else {
                statusColor = ConsoleColorUtils.BLUE;
            }

            if(!this.stripeConfig.getStripeMode().equals(StripeMode.TEST)) {
                log.info(ConsoleColorUtils.cyan("Request body:") + " {}", reqBody);
                // TODO: implement redacting for the response body
                log.info(ConsoleColorUtils.yellow("Request body:") + " {}", reqBody);
                log.info(statusColor + "Response status:" + " {}", status + ConsoleColorUtils.RESET);
            } else {
                log.warn(ConsoleColorUtils.yellow("logging was ignored because stripe mode is test"));
            }
            
        } else {
            log.warn("Request or Response is not wrapped properly. Skipping body logging.");
        }
    }
}