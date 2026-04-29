-- Public registrations for cultural events (Task 22). DNI stored encrypted at rest.

CREATE TABLE event_attendees (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    cultural_event_id   BIGINT          NOT NULL,
    full_name           VARCHAR(255)    NOT NULL,
    email               VARCHAR(255)    NOT NULL,
    phone               VARCHAR(64),
    dni_encrypted       TEXT            NOT NULL,
    version             BIGINT          NOT NULL DEFAULT 0,
    created_at          DATETIME(6)     NOT NULL,
    updated_at          DATETIME(6)     NOT NULL,
    created_by          VARCHAR(180),
    last_modified_by    VARCHAR(180),

    CONSTRAINT pk_event_attendees PRIMARY KEY (id),
    CONSTRAINT fk_event_attendees_cultural_event
        FOREIGN KEY (cultural_event_id) REFERENCES cultural_events (id) ON DELETE RESTRICT,
    CONSTRAINT uq_event_attendees_event_email UNIQUE (cultural_event_id, email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_event_attendees_cultural_event_id ON event_attendees (cultural_event_id);
