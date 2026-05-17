-- Cultural events agenda (admin CRUD; public read in a later task).

CREATE TABLE cultural_events (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    title            VARCHAR(255)    NOT NULL,
    description      TEXT,
    starts_at        DATETIME(6)     NOT NULL,
    ends_at          DATETIME(6),
    location         VARCHAR(255),
    max_attendees    INT,
    status           VARCHAR(32)     NOT NULL DEFAULT 'DRAFT',
    version          BIGINT          NOT NULL DEFAULT 0,
    created_at       DATETIME(6)     NOT NULL,
    updated_at       DATETIME(6)     NOT NULL,
    created_by       VARCHAR(180),
    last_modified_by VARCHAR(180),

    CONSTRAINT pk_cultural_events PRIMARY KEY (id),
    CONSTRAINT ck_cultural_events_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_cultural_events_starts_at ON cultural_events (starts_at);
