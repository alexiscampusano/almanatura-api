-- Attendance / invitation tracking per activity and actor.

CREATE TABLE activity_participations (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    activity_id          BIGINT          NOT NULL,
    actor_id             BIGINT          NOT NULL,
    status               VARCHAR(32)     NOT NULL,
    version              BIGINT          NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    created_by           VARCHAR(180),
    last_modified_by     VARCHAR(180),

    CONSTRAINT pk_activity_participations PRIMARY KEY (id),
    CONSTRAINT fk_ap_activity
        FOREIGN KEY (activity_id) REFERENCES project_activities (id) ON DELETE CASCADE,
    CONSTRAINT fk_ap_actor
        FOREIGN KEY (actor_id) REFERENCES actors (id) ON DELETE RESTRICT,
    CONSTRAINT ck_ap_status CHECK (status IN (
        'INVITED', 'CONFIRMED', 'DECLINED', 'ATTENDED')),
    CONSTRAINT uq_activity_actor UNIQUE (activity_id, actor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_ap_activity_id ON activity_participations (activity_id);
CREATE INDEX ix_ap_actor_id ON activity_participations (actor_id);
