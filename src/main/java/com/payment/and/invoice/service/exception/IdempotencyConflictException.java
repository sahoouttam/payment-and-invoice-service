package com.payment.and.invoice.service.exception;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String messsage) {
        super(messsage);
    }
}
