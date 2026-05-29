package com.payment.and.invoice.service.model;

public enum WebhookEventType {
    INVOICE_CREATED("invoice.created"),
    INVOICE_PAID("invoice.paid"),
    INVOICE_PAYMENT_FAILED("invoice.payment_failed"),
    INVOICE_VOIDED("invoice.voided");
 
    private final String eventName;
 
    WebhookEventType(String eventName) {
        this.eventName = eventName;
    }
 
    public String getEventName() {
        return eventName;
    }
}
