package com.payment.and.invoice.service.exception;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String messsage) {
        super(messsage);
    }
}
