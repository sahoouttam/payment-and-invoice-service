package com.payment.and.invoice.service.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

import com.payment.and.invoice.service.model.InvoiceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceResponse {
    
    private Long invoiceId;
    private InvoiceStatus invoiceStatus;
    private Long businessId;
    private String businessName;
    private Long customerId;
    private String customerName;
    private Integer totalCents;
    private LocalDateTime dueDate;
    private List<LineItemResponse> lineItemResponses;
}
