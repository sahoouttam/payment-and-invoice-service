package com.payment.and.invoice.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.and.invoice.service.dtos.request.CreateInvoiceRequest;
import com.payment.and.invoice.service.dtos.request.PaymentRequest;
import com.payment.and.invoice.service.dtos.response.CreateInvoiceResponse;
import com.payment.and.invoice.service.dtos.response.InvoicePaymentResponse;
import com.payment.and.invoice.service.security.AuthenticationUtils;
import com.payment.and.invoice.service.service.InvoiceService;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<CreateInvoiceResponse> createInvoice(
                    @RequestBody CreateInvoiceRequest createInvoiceRequest) {
        Long businessId = AuthenticationUtils.getBusinessId();
        CreateInvoiceResponse createInvoiceResponse = invoiceService
                        .createInvoice(businessId, createInvoiceRequest);
        return new ResponseEntity<>(createInvoiceResponse, HttpStatus.CREATED);
    }

    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<InvoicePaymentResponse> processInvoicePayment(@PathVariable Long invoiceId,
                        @RequestHeader("Idempotency-Key") String idempotencyKey,
                        @RequestBody PaymentRequest paymentRequest) {
        Long businessId = AuthenticationUtils.getBusinessId();
        InvoicePaymentResponse invoicePaymentResponse = invoiceService
                .processPayment(invoiceId, idempotencyKey, paymentRequest, businessId);
        return new ResponseEntity<>(invoicePaymentResponse, HttpStatus.OK);
    }
}
