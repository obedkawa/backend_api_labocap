-- V25 — Table configuration MECeF et code_normalise sur invoices (story 5-3)

CREATE TABLE IF NOT EXISTS setting_invoices (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID        NOT NULL REFERENCES branches(id),
    token       TEXT,
    ifu         VARCHAR(50),
    status      BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID,
    deleted_at  TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_setting_invoices_branch ON setting_invoices(branch_id);

ALTER TABLE invoices
    ADD COLUMN IF NOT EXISTS code_normalise VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_invoices_code_mecef      ON invoices(code_mecef)      WHERE code_mecef IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_invoices_code_normalise  ON invoices(code_normalise)  WHERE code_normalise IS NOT NULL;
