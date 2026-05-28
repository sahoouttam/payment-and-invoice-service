package com.payment.and.invoice.service.exception;

public class ApiKeyNotFoundException extends RuntimeException {
    public ApiKeyNotFoundException(String messsage) {
        super(messsage);
    }
}
