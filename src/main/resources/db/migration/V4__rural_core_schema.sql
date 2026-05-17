-- Rural platform core: actors, projects (with pillar), applications.
-- Drops legacy cultural agenda tables (no backward data migration).

DROP TABLE IF EXISTS event_attendees;
DROP TABLE IF EXISTS cultural_events;

CREATE TABLE actors (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    full_name            VARCHAR(255)    NOT NULL,
    region               VARCHAR(120),
    version              BIGINT          NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    created_by           VARCHAR(180),
    last_modified_by     VARCHAR(180),

    CONSTRAINT pk_actors PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_actors_full_name ON actors (full_name);

CREATE TABLE projects (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    title                VARCHAR(255)    NOT NULL,
    description          TEXT,
    pillar               VARCHAR(32)     NOT NULL,
    status               VARCHAR(32)     NOT NULL DEFAULT 'DRAFT',
    starts_at            DATETIME(6),
    ends_at              DATETIME(6),
    location             VARCHAR(255),
    version              BIGINT          NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    created_by           VARCHAR(180),
    last_modified_by     VARCHAR(180),

    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT ck_projects_pillar CHECK (pillar IN (
        'TECHNOLOGY', 'EDUCATION', 'ENTREPRENEURSHIP', 'HEALTH', 'CULTURE')),
    CONSTRAINT ck_projects_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_projects_pillar ON projects (pillar);
CREATE INDEX ix_projects_status ON projects (status);
CREATE INDEX ix_projects_starts_at ON projects (starts_at);

CREATE TABLE applications (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    project_id           BIGINT          NOT NULL,
    actor_id             BIGINT,
    status               VARCHAR(32)     NOT NULL,
    full_name            VARCHAR(255)    NOT NULL,
    email                VARCHAR(255)    NOT NULL,
    phone                VARCHAR(64),
    dni_encrypted        TEXT            NOT NULL,
    version              BIGINT          NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,
    created_by           VARCHAR(180),
    last_modified_by     VARCHAR(180),

    CONSTRAINT pk_applications PRIMARY KEY (id),
    CONSTRAINT fk_applications_project
        FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE RESTRICT,
    CONSTRAINT fk_applications_actor
        FOREIGN KEY (actor_id) REFERENCES actors (id) ON DELETE RESTRICT,
    CONSTRAINT ck_applications_status CHECK (status IN (
        'SUBMITTED', 'UNDER_REVIEW', 'REJECTED', 'NEEDS_INFO', 'APPROVED',
        'REGISTERED_AS_ACTOR')),
    CONSTRAINT uq_applications_project_email UNIQUE (project_id, email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX ix_applications_project_id ON applications (project_id);
CREATE INDEX ix_applications_actor_id ON applications (actor_id);
CREATE INDEX ix_applications_status ON applications (status);
