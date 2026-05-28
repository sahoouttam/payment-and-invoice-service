package com.payment.and.invoice.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.and.invoice.service.model.PaymentAttempt;

@Repository
public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {
    Optional<PaymentAttempt> findByIdempotencyKey(String idempotencyKey);
}
