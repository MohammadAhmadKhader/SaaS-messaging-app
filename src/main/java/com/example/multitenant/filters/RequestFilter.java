package com.example.multitenant.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.services.security.RateLimiterService;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    RateLimiterService rateLimiterService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
        var clientIP = rateLimiterService.getClientIp(request);
        var bucket = rateLimiterService.getBucketByIP(clientIP);
        
        if(bucket.tryConsume(1)){
            filterChain.doFilter(request, response);
        }else {
            ApiResponses.SendErrTooManyRequests(response);
        }
    }
    
}
