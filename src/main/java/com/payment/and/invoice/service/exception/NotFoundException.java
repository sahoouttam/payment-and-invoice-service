package com.payment.and.invoice.service.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String messsage) {
        super(messsage);
    }
}
