package com.payment.and.invoice.service.exception;

public class PSPException extends RuntimeException {
    public PSPException(String messsage) {
        super(messsage);
    }

    public PSPException(String messsage, Exception exception) {
        super(messsage, exception);
    }
}
