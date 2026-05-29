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
public class ApiKeyResponse {
    private Long id;
    private Long businessId;
    private String keyName;
    private String keyPrefix;
    private boolean active;
    private boolean revoked;
    private LocalDateTime revokedAt;
    private LocalDateTime lastUsedAt;
}
