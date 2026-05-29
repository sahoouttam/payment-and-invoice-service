package com.payment.and.invoice.service.dtos.response;

import com.payment.and.invoice.service.model.PaymentAttemptStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAttemptResponse {
    
    private Long id;

    private Integer amountCents;

    private PaymentAttemptStatus paymentAttemptStatus;

    private String pspReference;
 
    private String failureCode;
 
    private String errorMessage;
}
