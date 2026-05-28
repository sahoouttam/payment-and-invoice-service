package com.payment.and.invoice.service.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.repository.PaymentAttemptRepository;

@Slf4j
@Service
public class PaymentAttemptService {
    
    private final PaymentAttemptRepository paymentAttemptRepository;

    @Autowired
    public PaymentAttemptService(PaymentAttemptRepository paymentAttemptRepository) {
        this.paymentAttemptRepository = paymentAttemptRepository;
    }

    
}
