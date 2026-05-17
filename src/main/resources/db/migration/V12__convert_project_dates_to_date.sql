-- Convert project date columns from DATETIME(6) to DATE (remove time component).
ALTER TABLE projects
    MODIFY COLUMN starts_at DATE NULL,
    MODIFY COLUMN ends_at DATE NULL;
