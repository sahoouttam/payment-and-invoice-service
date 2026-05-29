package com.payment.and.invoice.service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateApiKeyResponse {
    
    private String apiKey;
    private String keyName;
    private String message;
}
