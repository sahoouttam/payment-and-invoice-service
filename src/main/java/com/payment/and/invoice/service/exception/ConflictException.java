package com.payment.and.invoice.service.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String messsage) {
        super(messsage);
    }
}
