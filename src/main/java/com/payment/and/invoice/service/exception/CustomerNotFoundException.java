package com.payment.and.invoice.service.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String messsage) {
        super(messsage);
    }

    public CustomerNotFoundException(String messsage, Exception exception) {
        super(messsage, exception);
    }
}
