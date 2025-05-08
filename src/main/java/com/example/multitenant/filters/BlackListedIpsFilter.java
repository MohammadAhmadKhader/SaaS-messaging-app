package com.example.multitenant.filters;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.services.cache.BlackListedIpsCacheService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BlackListedIpsFilter extends OncePerRequestFilter  {
    private final BlackListedIpsCacheService blackListedIPsCacheService;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain) throws IOException, ServletException {
        var isBlackListed = this.blackListedIPsCacheService.isBlackListedIp(req);
        if(isBlackListed) {
            ApiResponses.SendBlockedIpResponse(res);
            return;
        }

        filterChain.doFilter(req, res);
    }
}