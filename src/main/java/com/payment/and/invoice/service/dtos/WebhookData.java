package com.payment.and.invoice.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookData {
    
    private Long invoiceId;
    private Long businessId;
    private Long customerId;
    private String invoiceStatus;
    private Integer totalCents;
    private String dueDate;
}
