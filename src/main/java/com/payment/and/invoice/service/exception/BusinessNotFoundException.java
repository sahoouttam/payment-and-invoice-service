package com.payment.and.invoice.service.exception;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(String messsage) {
        super(messsage);
    }
}
