-- Initial schema: internal users for the AlmaNatura platform.
-- Roles are stored as VARCHAR (matches Spring Security convention) and constrained
-- to the values defined in com.almanatura.api.enums.Role.

CREATE TABLE users (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(120)    NOT NULL,
    email           VARCHAR(180)    NOT NULL,
    password_hash   VARCHAR(100)    NOT NULL,
    role            VARCHAR(32)     NOT NULL,
    enabled         BIT(1)          NOT NULL DEFAULT b'1',
    version         BIGINT          NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NOT NULL,
    created_by      VARCHAR(180),
    last_modified_by VARCHAR(180),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('SUPER_USER', 'EVENT_MANAGER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
