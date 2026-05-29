package com.payment.and.invoice.service.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.client.PSPClient;
import com.payment.and.invoice.service.dtos.request.PSPChargeRequest;
import com.payment.and.invoice.service.dtos.response.PSPChargeResponse;
import com.payment.and.invoice.service.dtos.response.PaymentAttemptResponse;
import com.payment.and.invoice.service.exception.IdempotencyConflictException;
import com.payment.and.invoice.service.exception.PSPException;
import com.payment.and.invoice.service.exception.PaymentProcessingException;
import com.payment.and.invoice.service.model.Invoice;
import com.payment.and.invoice.service.model.PaymentAttempt;
import com.payment.and.invoice.service.model.PaymentAttemptStatus;
import com.payment.and.invoice.service.repository.PaymentAttemptRepository;

@Slf4j
@Service
public class PaymentAttemptService {
    
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final PSPClient pspClient;

    @Autowired
    public PaymentAttemptService(PaymentAttemptRepository paymentAttemptRepository,
                                 PSPClient pspClient) {
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.pspClient = pspClient;
    }

    public PaymentAttemptResponse processPaymentAttempt(Invoice invoice, String idempotentKey, PSPChargeRequest pspChargeRequest) {
        Optional<PaymentAttempt> existingAttempt = paymentAttemptRepository
                        .findByIdempotencyKey(idempotentKey);
        if (existingAttempt.isPresent()) {
            log.info("Idempotent request found. Payment ID: {}, Status: {}", 
                     existingAttempt.get().getId(), 
                     existingAttempt.get().getPaymentAttemptStatus());
            if (existingAttempt.get().getInvoice().getId() != invoice.getId()) {
                throw new IdempotencyConflictException(
                    "Idempotency key already used for different invoice. " +
                    "Original: " + existingAttempt.get().getInvoice().getId() + 
                    ", Requested: " + invoice.getId());
            }
            return mapToResponse(existingAttempt.get());
        }
        PaymentAttempt paymentAttempt = PaymentAttempt.builder()
                                    .invoice(invoice)
                                    .idempotencyKey(idempotentKey)
                                    .cardToken(pspChargeRequest.getCardToken())
                                    .amountCents(pspChargeRequest.getAmountCents())
                                    .paymentAttemptStatus(PaymentAttemptStatus.PENDING)
                                    .build();
        paymentAttempt = paymentAttemptRepository.save(paymentAttempt);
        try {
            PSPChargeResponse pspChargeResponse = pspClient.charge(pspChargeRequest);
            if (pspChargeResponse.isSuccess()) {
                log.info("PSP returned SUCCESS. PSP Ref: {}", pspChargeResponse.getPspRef());
                paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.SUCCESS);
                paymentAttempt.setPspReference(pspChargeResponse.getPspRef());
                paymentAttemptRepository.save(paymentAttempt);
            } else if (pspChargeResponse.isFailed()) {
                log.warn("PSP returned FAILED. Code: {}", pspChargeResponse.getCode());
                paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.FAILED);
                paymentAttempt.setPspReference(pspChargeResponse.getPspRef());
                paymentAttempt.setFailureCode(pspChargeResponse.getCode());
                paymentAttemptRepository.save(paymentAttempt);
            }
            return mapToResponse(paymentAttempt);
        } catch (PSPException exception) {
            log.error("PSP call failed with exception", exception);
            paymentAttempt.setPaymentAttemptStatus(PaymentAttemptStatus.FAILED);
            paymentAttempt.setErrorMessage(exception.getMessage());
            paymentAttemptRepository.save(paymentAttempt);
            throw new PaymentProcessingException("Payment processing failed: " 
                            + exception.getMessage());
        }
    }

    private PaymentAttemptResponse mapToResponse(PaymentAttempt paymentAttempt) {
        return PaymentAttemptResponse.builder()
                            .id(paymentAttempt.getId())
                            .amountCents(paymentAttempt.getAmountCents())
                            .paymentAttemptStatus(paymentAttempt.getPaymentAttemptStatus())
                            .pspReference(paymentAttempt.getPspReference())
                            .failureCode(paymentAttempt.getFailureCode())
                            .errorMessage(paymentAttempt.getErrorMessage())
                            .build();
    }
}
