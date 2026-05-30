package com.payment.and.invoice.service.dtos.response;

import java.time.LocalDateTime;

import com.payment.and.invoice.service.model.WebhookDeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDeliveryResponse {
    
    private Long id;

    private String webhookEventType;
    
    private String endpointUrl;

    private WebhookDeliveryStatus webhookDeliveryStatus;
    
    private int attemptCount;
    
    private LocalDateTime deliveredAt;
    
    private LocalDateTime nextAttemptAt;

    private Integer lastResponseCode;

    private String lastError;

    private LocalDateTime createdAt;
}
