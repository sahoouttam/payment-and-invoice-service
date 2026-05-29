package com.payment.and.invoice.service.dtos;

import java.time.LocalDateTime;

import com.payment.and.invoice.service.model.InvoiceStatus;

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
    private InvoiceStatus invoiceStatus;
    private Integer totalCents;
    private LocalDateTime dueDate;
}
