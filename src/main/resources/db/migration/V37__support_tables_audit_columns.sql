-- V37 — Ajout colonnes audit sur les tables du module support

ALTER TABLE ticket_comments
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS created_by  UUID NULL,
    ADD COLUMN IF NOT EXISTS updated_by  UUID NULL;

CREATE INDEX IF NOT EXISTS idx_ticket_comments_deleted_at ON ticket_comments(deleted_at);

ALTER TABLE signals
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS created_by  UUID NULL,
    ADD COLUMN IF NOT EXISTS updated_by  UUID NULL;

CREATE INDEX IF NOT EXISTS idx_signals_deleted_at ON signals(deleted_at);

ALTER TABLE problem_categories
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS created_by  UUID NULL,
    ADD COLUMN IF NOT EXISTS updated_by  UUID NULL;

CREATE INDEX IF NOT EXISTS idx_problem_categories_deleted_at ON problem_categories(deleted_at);

ALTER TABLE problem_reports
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS created_by  UUID NULL,
    ADD COLUMN IF NOT EXISTS updated_by  UUID NULL;

CREATE INDEX IF NOT EXISTS idx_problem_reports_deleted_at ON problem_reports(deleted_at);
