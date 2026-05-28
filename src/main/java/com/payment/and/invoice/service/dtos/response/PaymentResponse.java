package com.payment.and.invoice.service.dtos.response;

import com.payment.and.invoice.service.model.InvoiceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long invoiceId; 
    private InvoiceStatus invoiceStatus; 
    private String pspRef; 
    private String message;
}
