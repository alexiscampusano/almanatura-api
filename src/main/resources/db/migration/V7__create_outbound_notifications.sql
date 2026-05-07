-- Notification intent log (MVP: no external provider; records outbound "would send" events).

CREATE TABLE outbound_notifications (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    channel              VARCHAR(32)     NOT NULL,
    recipient_hint       VARCHAR(255)    NOT NULL,
    subject              VARCHAR(500),
    body                 TEXT,
    status               VARCHAR(32)     NOT NULL DEFAULT 'PENDING',
    version              BIGINT          NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    created_by           VARCHAR(180),
    last_modified_by     VARCHAR(180),

    CONSTRAINT pk_outbound_notifications PRIMARY KEY (id),
    CONSTRAINT ck_on_channel CHECK (channel IN ('EMAIL')),
    CONSTRAINT ck_on_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_on_status ON outbound_notifications (status);
