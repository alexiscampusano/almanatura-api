-- Simple impact / follow-up metrics per project (admin-managed).

CREATE TABLE project_impact_entries (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    project_id           BIGINT          NOT NULL,
    recorded_at          DATETIME(6)     NOT NULL,
    metric_label         VARCHAR(255)    NOT NULL,
    numeric_value        DECIMAL(19, 4),
    notes                TEXT,
    version              BIGINT          NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    created_by           VARCHAR(180),
    last_modified_by     VARCHAR(180),

    CONSTRAINT pk_project_impact_entries PRIMARY KEY (id),
    CONSTRAINT fk_pie_project
        FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_pie_project_id ON project_impact_entries (project_id);
CREATE INDEX ix_pie_recorded_at ON project_impact_entries (recorded_at);
