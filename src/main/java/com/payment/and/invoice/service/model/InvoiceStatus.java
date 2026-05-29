package com.payment.and.invoice.service.model;

public enum InvoiceStatus {
    DRAFT, OPEN, PENDING, PAID, VOID, UNCOLLECTIBLE;

    public boolean isTerminal() {
        return this == PAID || this == VOID || this == UNCOLLECTIBLE;
    }
 
    public boolean canTransitionTo(InvoiceStatus next) {
        return switch (this) {
            case DRAFT -> next == OPEN || next == VOID;
            case OPEN -> next == PENDING || next == VOID || next == UNCOLLECTIBLE;
            case PENDING -> next == PAID || next == OPEN;
            case PAID, VOID, UNCOLLECTIBLE -> false;
        };
    }
}


