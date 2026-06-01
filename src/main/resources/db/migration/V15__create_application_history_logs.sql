CREATE TABLE application_history_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    old_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    
    -- BaseAuditableEntity columns
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    created_by VARCHAR(180),
    last_modified_by VARCHAR(180),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_app_history_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
);
