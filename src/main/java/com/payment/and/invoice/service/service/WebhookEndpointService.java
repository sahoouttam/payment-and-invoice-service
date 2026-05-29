package com.payment.and.invoice.service.service;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.and.invoice.service.dtos.WebhookPayload;
import com.payment.and.invoice.service.dtos.request.WebhookEndpointRequest;
import com.payment.and.invoice.service.dtos.response.WebhookEndpointResponse;
import com.payment.and.invoice.service.exception.NotFoundException;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.model.WebhookDelivery;
import com.payment.and.invoice.service.model.WebhookDeliveryStatus;
import com.payment.and.invoice.service.model.WebhookEndpoint;
import com.payment.and.invoice.service.repository.WebhookEndpointRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebhookEndpointService {
    
    private final WebhookEndpointRepository webhookEndpointRepository;
    private final WebhookDeliveryService webhookDeliveryService;
    private final BusinessService businessService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired
    public WebhookEndpointService(WebhookEndpointRepository webhookEndpointRepository,
                                WebhookDeliveryService webhookDeliveryService,
                                BusinessService businessService) {
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.webhookDeliveryService = webhookDeliveryService;
        this.businessService = businessService;
    }

    public WebhookEndpointResponse registerWebhook(Long businessId, 
                            WebhookEndpointRequest webhookEndpointRequest) {
        Business business = businessService.findBusinessById(businessId);
        boolean webhookExists = webhookEndpointRepository
                    .existsByBusinessAndUrl(business, webhookEndpointRequest.getUrl());
        if (webhookExists) {
            throw new RuntimeException("Webhook endpoint already exists for this URL");
        }

        WebhookEndpoint webhookEndpoint = WebhookEndpoint.builder()
                                .business(business)
                                .url(webhookEndpointRequest.getUrl())
                                .description(webhookEndpointRequest.getDescription())
                                .active(true)
                                .build();
        WebhookEndpoint savedWebhookEndpoint = webhookEndpointRepository.save(webhookEndpoint);
        return mapToResponse(savedWebhookEndpoint);
    }

    public List<WebhookEndpointResponse> getEndpoints(Long businessId) {
        Business business = businessService.findBusinessById(businessId);
        return webhookEndpointRepository.findByBusiness(business)
                                    .stream()
                                    .map(webhookEndpoint -> mapToResponse(webhookEndpoint))
                                    .collect(Collectors.toList());
    }

    public void deleteEndpoint(Long endpointId, Long businessId) {
        Business business = businessService.findBusinessById(businessId);
        WebhookEndpoint webhookEndpoint = webhookEndpointRepository
                        .findByIdAndBusiness(endpointId, business)
                        .orElseThrow(() -> new NotFoundException("webhook endpoint not found"));
        
        webhookEndpoint.setActive(false);
        webhookEndpointRepository.save(webhookEndpoint);
        log.info("Deactivated webhook endpoint: {}", endpointId);
    }

    public void sendWebhook(WebhookPayload webhookPayload) {
        Long businessId = webhookPayload.getWebhookData().getBusinessId();
        Business business = businessService.findBusinessById(businessId);
        List<WebhookEndpoint> webhookEndpoints = webhookEndpointRepository
                            .findByBusinessAndActive(business, true);
        if (webhookEndpoints.isEmpty()) {
            log.debug("No active webhook endpoints for business: {}", businessId);
            return;
        }
        log.info("Sending webhook for event: {}, invoice: {}, endpoints: {}", 
                        webhookPayload.getWebhookEventType().getEventName(), 
                        webhookPayload.getWebhookData().getInvoiceId(),
                        webhookEndpoints.size());
        
        for (WebhookEndpoint webhookEndpoint : webhookEndpoints) {
            try {
                String payloadJson = objectMapper.writeValueAsString(webhookPayload);
                WebhookDelivery webhookDelivery = WebhookDelivery.builder()
                                        .webhookEndpoint(webhookEndpoint)
                                        .webhookEventType(webhookPayload.getWebhookEventType())
                                        .payload(payloadJson)
                                        .webhookDeliveryStatus(WebhookDeliveryStatus.PENDING)
                                        .attemptCount(0)
                                        .nextAttemptAt(LocalDateTime.now().plusMinutes(1))
                                        .build();
                
            } catch (Exception e) {
                log.error("Failed to create webhook delivery for endpoint: {}", endpoint.getId(), e);
            }
        }
    }

    private WebhookEndpointResponse mapToResponse(WebhookEndpoint webhookEndpoint) {
        return WebhookEndpointResponse.builder()
                    .id(webhookEndpoint.getId())
                    .url(webhookEndpoint.getUrl())
                    .description(webhookEndpoint.getDescription())
                    .active(webhookEndpoint.getActive())
                    .createdAt(webhookEndpoint.getCreatedAt())
                    .build();

    }
}
