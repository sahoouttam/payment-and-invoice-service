package com.payment.and.invoice.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.repository.WebhookEndpointRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebhookEndpointService {
    
    private final WebhookEndpointRepository webhookEndpointRepository;

    @Autowired
    public WebhookEndpointService(WebhookEndpointRepository webhookEndpointRepository) {
        this.webhookEndpointRepository = webhookEndpointRepository;
    }

    
}
