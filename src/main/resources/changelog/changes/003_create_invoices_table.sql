CREATE TABLE invoices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    business_id BIGINT,
    customer_id BIGINT,
    invoice_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    total_cents INTEGER NOT NULL DEFAULT 0,
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_invoices_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE SET NULL,
    CONSTRAINT fk_invoices_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    CONSTRAINT chk_invoice_status CHECK (invoice_status IN ('DRAFT', 'OPEN', 'PAID', 'VOID', 'UNCOLLECTIBLE'))
);