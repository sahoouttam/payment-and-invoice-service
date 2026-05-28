package com.payment.and.invoice.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class PSPConfig {
    
    @Value("${psp.base-url}")
    private String pspBaseUrl;

    @Value("${psp.read-timeout-ms}") 
    private int readTimeoutMs;

    @Value("${psp.connect-timeout-ms}")
    private int connectTimeoutMs;

    @Value("${psp.endpoints.charge}")
    private String chargeEndpoint;
}
