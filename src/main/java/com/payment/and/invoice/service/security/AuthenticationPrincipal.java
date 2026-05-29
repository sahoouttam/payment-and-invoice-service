package com.payment.and.invoice.service.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationPrincipal {
    
    private Long businessId;
    private Long apiKeyId;
}
