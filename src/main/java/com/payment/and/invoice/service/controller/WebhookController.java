package com.payment.and.invoice.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.and.invoice.service.dtos.request.WebhookEndpointRequest;
import com.payment.and.invoice.service.dtos.response.WebhookDeliveryResponse;
import com.payment.and.invoice.service.dtos.response.WebhookEndpointResponse;
import com.payment.and.invoice.service.security.AuthenticationUtils;
import com.payment.and.invoice.service.service.WebhookEndpointService;


@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {
    
    private final WebhookEndpointService webhookEndpointService;

    @Autowired
    public WebhookController(WebhookEndpointService webhookEndpointService) {
        this.webhookEndpointService = webhookEndpointService;
    }

    @PostMapping("/endpoints")
    public ResponseEntity<WebhookEndpointResponse> registerWebhookEndoint(
                @RequestBody WebhookEndpointRequest webhookEndpointRequest) {
        Long businessId = AuthenticationUtils.getBusinessId();
        WebhookEndpointResponse webhookEndpointResponse = 
                        webhookEndpointService.registerWebhook(businessId, webhookEndpointRequest);
        return new ResponseEntity<>(webhookEndpointResponse, HttpStatus.CREATED);
    }

    @GetMapping("/endpoints")
    public ResponseEntity<List<WebhookEndpointResponse>> getAllWebhookEndpoints() {
        Long businessId = AuthenticationUtils.getBusinessId();
        List<WebhookEndpointResponse> webhookEndpointResponses = 
                webhookEndpointService.findAllWebhookEndpoints(businessId);
        return new ResponseEntity<>(webhookEndpointResponses, HttpStatus.OK);
    }

    @DeleteMapping("/endpoints/{endpointId}")
    public ResponseEntity<Void> deleteWebhookEndpoint(@PathVariable Long endpointId) {
        Long businessId = AuthenticationUtils.getBusinessId();
        webhookEndpointService.deleteEndpoint(endpointId, businessId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/deliveries")
    public ResponseEntity<List<WebhookDeliveryResponse>> getAllWebhookDeliveries() {
        Long businessId = AuthenticationUtils.getBusinessId();
        List<WebhookDeliveryResponse> webhookDeliveryResponses = 
                    webhookEndpointService.findAllWebhookDeliveries(businessId);
        return new ResponseEntity<>(webhookDeliveryResponses, HttpStatus.OK);
    }
}

