package com.payment.and.invoice.service.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.payment.and.invoice.service.model.ApiKey;
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
                ApiKey currentApiKey = apiKeyService.validateApiKey(apiKey);
                AuthenticationPrincipal authenticationPrincipal = 
                            new AuthenticationPrincipal(
                                currentApiKey.getBusiness().getId(),
                                currentApiKey.getId()
                            );
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        authenticationPrincipal, null, Collections.emptyList());
                
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                log.error("Error validating API key: {}", e.getMessage(), e);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/health") || path.startsWith("/psp/");
    }
}
