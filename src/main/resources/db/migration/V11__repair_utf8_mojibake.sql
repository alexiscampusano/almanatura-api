-- Repair text that was stored as UTF-8 bytes interpreted as Latin-1 (e.g. "DemogrÃ¡fico" -> "Demográfico").
-- Only rows containing typical mojibake markers are updated.

UPDATE projects
SET title = CONVERT(CAST(CONVERT(title USING latin1) AS BINARY) USING utf8mb4)
WHERE title LIKE '%Ã%';

UPDATE projects
SET description = CONVERT(CAST(CONVERT(description USING latin1) AS BINARY) USING utf8mb4)
WHERE description LIKE '%Ã%';

UPDATE projects
SET location = CONVERT(CAST(CONVERT(location USING latin1) AS BINARY) USING utf8mb4)
WHERE location LIKE '%Ã%';

UPDATE actors
SET full_name = CONVERT(CAST(CONVERT(full_name USING latin1) AS BINARY) USING utf8mb4)
WHERE full_name LIKE '%Ã%';

UPDATE actors
SET region = CONVERT(CAST(CONVERT(region USING latin1) AS BINARY) USING utf8mb4)
WHERE region LIKE '%Ã%';

UPDATE project_impact_entries
SET metric_label = CONVERT(CAST(CONVERT(metric_label USING latin1) AS BINARY) USING utf8mb4)
WHERE metric_label LIKE '%Ã%';

UPDATE project_impact_entries
SET notes = CONVERT(CAST(CONVERT(notes USING latin1) AS BINARY) USING utf8mb4)
WHERE notes LIKE '%Ã%';
