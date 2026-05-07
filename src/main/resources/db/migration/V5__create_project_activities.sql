-- Project planning: scheduled activities (encounters / milestones) per project.

CREATE TABLE project_activities (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    project_id           BIGINT          NOT NULL,
    title                VARCHAR(255)    NOT NULL,
    description          TEXT,
    starts_at            DATETIME(6)     NOT NULL,
    ends_at              DATETIME(6),
    location             VARCHAR(255),
    status               VARCHAR(32)     NOT NULL DEFAULT 'SCHEDULED',
    version              BIGINT          NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    created_by           VARCHAR(180),
    last_modified_by     VARCHAR(180),

    CONSTRAINT pk_project_activities PRIMARY KEY (id),
    CONSTRAINT fk_project_activities_project
        FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT ck_project_activities_status CHECK (status IN (
        'SCHEDULED', 'CANCELLED', 'COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_project_activities_project_id ON project_activities (project_id);
CREATE INDEX ix_project_activities_starts_at ON project_activities (starts_at);
