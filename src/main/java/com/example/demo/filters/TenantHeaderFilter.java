package com.example.demo.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantHeaderFilter extends OncePerRequestFilter {

    @Value("${custom.passkey.bypass.tenant-filter:false}")
    private boolean bypassFilters;
    
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final List<String> EXCLUDED_PATHS = List.of(
        "/api/app-dashboard", 
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/user",
        "/api/auth/logout"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch((str) -> path.startsWith(str));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(this.bypassFilters) {
            filterChain.doFilter(request, response);
            return;
        }

        var tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Missing required header: " + TENANT_HEADER + "\"}");

            return;
        }

        try {
            Integer.parseInt(tenantId);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":" + "\"" + String.format("Invalid tenantId received: '%s' must be an integer", tenantId) + "\"}");
            
            return;
        }

        filterChain.doFilter(request, response);
    }
}
