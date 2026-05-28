package com.payment.and.invoice.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.and.invoice.service.dtos.request.PaymentRequest;
import com.payment.and.invoice.service.dtos.response.PaymentResponse;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<PaymentResponse> payInvoice(
                        @RequestHeader("X-API-Key") String apiKey,
                        @PathVariable String invoiceId,
                        @RequestHeader("Idempotency-Key") String idempotencyKey,
                        @RequestBody PaymentRequest request) {
        return null;
    }


}
