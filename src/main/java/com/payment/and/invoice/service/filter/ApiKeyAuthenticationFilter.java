package com.payment.and.invoice.service.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.service.ApiKeyService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    private final ApiKeyService apiKeyService;

    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-Key");

        if (apiKey != null && !apiKey.isEmpty()) {
            try {
                Business business = apiKeyService.validateApiKey(apiKey);
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        business, apiKey, Collections.emptyList());
                
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                log.debug("Authenticated business: {} (ID: {})", 
                             business.getName(), business.getId());
            } catch (Exception e) {
                log.error("Error validating API key: {}", e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
