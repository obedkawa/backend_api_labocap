-- V32 — employee_documents: colonnes manquantes pour AuditableEntity et file_size

ALTER TABLE employee_documents
    ADD COLUMN IF NOT EXISTS file_size  BIGINT,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS created_by UUID,
    ADD COLUMN IF NOT EXISTS updated_by UUID;

CREATE INDEX IF NOT EXISTS idx_employee_documents_deleted_at  ON employee_documents(deleted_at);
CREATE INDEX IF NOT EXISTS idx_employee_documents_employee_id ON employee_documents(employee_id);
