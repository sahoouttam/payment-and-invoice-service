CREATE TABLE payment_attempts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    card_token VARCHAR(255),
    amount_cents INTEGER NOT NULL,
    payment_attempt_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    psp_reference VARCHAR(255),
    failure_code VARCHAR(100),
    error_message TEXT,
    idempotency_key VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_payment_attempts_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_attempt_status CHECK (payment_attempt_status IN ('SUCCESS', 'FAILED', 'PENDING'))
);