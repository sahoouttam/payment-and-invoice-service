CREATE TABLE line_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_id BIGINT,
    description VARCHAR(255),
    quantity INTEGER,
    unit_amount_cents INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_line_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);