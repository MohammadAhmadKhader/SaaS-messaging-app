package com.example.multitenant.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.multitenant.dtos.apiresponse.ApiResponses;

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
        "/api/auth/logout",
        "/api/users/search",
        "/api/organizations/search",
        "/ws",
        "/webhook",
        "/actuator"
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
            ApiResponses.SendErrMissingRequiredHeader(response, TENANT_HEADER);
            return;
        }

        try {
            Integer.parseInt(tenantId);
        } catch (Exception e) {
            ApiResponses.SendErrInvalidTenantId(response, tenantId);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
