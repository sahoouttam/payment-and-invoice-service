package com.payment.and.invoice.service.dtos.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEndpointResponse {
    
    private Long id;
    private String url;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
}
