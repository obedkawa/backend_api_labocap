-- V36 — Fix setting_invoices: convert from generic key/value to MECeF-specific columns
-- V3 created a key/value table; V25 used IF NOT EXISTS (was skipped). Fix here.

DROP INDEX IF EXISTS idx_setting_invoices_key_branch;

ALTER TABLE setting_invoices
    DROP COLUMN IF EXISTS key,
    DROP COLUMN IF EXISTS value;

ALTER TABLE setting_invoices
    ADD COLUMN IF NOT EXISTS ifu        VARCHAR(50)     NULL,
    ADD COLUMN IF NOT EXISTS token      TEXT            NULL,
    ADD COLUMN IF NOT EXISTS status     BOOLEAN         NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP       NULL,
    ADD COLUMN IF NOT EXISTS created_by UUID            NULL,
    ADD COLUMN IF NOT EXISTS updated_by UUID            NULL;

CREATE INDEX IF NOT EXISTS idx_setting_invoices_deleted_at ON setting_invoices(deleted_at);
