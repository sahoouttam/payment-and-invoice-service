package com.payment.and.invoice.service.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEndpointRequest {
    
    private String url;

    private String description;
}
