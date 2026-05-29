package com.payment.and.invoice.service.dtos;

import java.time.LocalDateTime;

import com.payment.and.invoice.service.model.WebhookEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayload {
    
    private WebhookEventType webhookEventType;
    private LocalDateTime timestamp;
    private WebhookData webhookData;
}
