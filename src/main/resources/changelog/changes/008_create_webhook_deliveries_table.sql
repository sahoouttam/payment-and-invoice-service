CREATE TABLE webhook_deliveries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    webhook_endpoint_id BIGINT,  
    webhook_event_type VARCHAR(50) NOT NULL,
    payload TEXT NOT NULL,
    webhook_delivery_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempt_count INTEGER NOT NULL DEFAULT 0,
    delivered_at TIMESTAMP,
    next_attempt_at TIMESTAMP,
    last_response_code INTEGER,  
    last_error TEXT,             
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_webhook_deliveries_endpoint FOREIGN KEY (webhook_endpoint_id) 
        REFERENCES webhook_endpoints(id) ON DELETE SET NULL,
    
    CONSTRAINT chk_webhook_delivery_status CHECK (
        webhook_delivery_status IN ('PENDING', 'DELIVERED', 'FAILED', 'EXHAUSTED')
    ),
    CONSTRAINT chk_webhook_event_type CHECK (
        webhook_event_type IN ('INVOICE_CREATED', 'INVOICE_PAID', 'INVOICE_PAYMENT_FAILED', 'INVOICE_VOIDED')
    )
);