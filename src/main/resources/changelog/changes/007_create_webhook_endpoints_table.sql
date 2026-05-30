CREATE TABLE webhook_endpoints (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    business_id BIGINT,  
    url VARCHAR(500) NOT NULL,  
    description VARCHAR(255),
    active BOOLEAN DEFAULT TRUE, 
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_webhook_endpoints_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);