-- Remove the planning/attendance layer so the backend keeps only projects.

DROP TABLE IF EXISTS activity_participations;
DROP TABLE IF EXISTS project_activities;