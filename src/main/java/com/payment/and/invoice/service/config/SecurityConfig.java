package com.payment.and.invoice.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.payment.and.invoice.service.security.ApiKeyAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    public SecurityConfig(ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health", "/psp/**").permitAll()
                .requestMatchers("/api/v1/businesses").permitAll() 
                .requestMatchers("/api/v1/keys").permitAll() 
                .requestMatchers("/api/v1/keys/**").permitAll()   
                .anyRequest().authenticated()
            )
            .addFilterBefore(apiKeyAuthenticationFilter, 
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
