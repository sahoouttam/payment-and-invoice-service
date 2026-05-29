package com.payment.and.invoice.service.dtos.response;

import com.payment.and.invoice.service.model.InvoiceStatus;
import com.payment.and.invoice.service.model.PaymentAttemptStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePaymentResponse {
    
    private Long invoiceId; 
    private InvoiceStatus invoiceStatus; 
    private Long paymentAttemptId;
    private Integer totalCents;
    private String pspReference;
    private String errorMessage;
}
