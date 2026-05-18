-- V28 — Story 6-1 : Caisses — colonnes audit AuditableEntity + table cashbox_operations

-- 1. Colonnes manquantes sur cashboxes pour AuditableEntity (created_by, updated_by, deleted_at)
ALTER TABLE cashboxes
    ADD COLUMN IF NOT EXISTS created_by UUID,
    ADD COLUMN IF NOT EXISTS updated_by UUID,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_cashboxes_deleted_at ON cashboxes(deleted_at) WHERE deleted_at IS NULL;

-- 2. Table cashbox_operations (anciennement cashbox_adds dans Laravel)
-- Chaque mouvement de caisse (CREDIT ou DEBIT) est tracé ici
CREATE TABLE IF NOT EXISTS cashbox_operations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NOT NULL,
    cashbox_id      UUID NOT NULL REFERENCES cashboxes(id),
    amount          DECIMAL(12,2) NOT NULL,
    type            VARCHAR(10) NOT NULL CHECK (type IN ('CREDIT', 'DEBIT')),
    description     TEXT,
    operation_date  DATE NOT NULL DEFAULT CURRENT_DATE,
    reference       VARCHAR(100),
    cheque_number   VARCHAR(50),
    attachement     VARCHAR(500),
    bank_id         UUID,
    invoice_id      UUID,
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID
);

CREATE INDEX IF NOT EXISTS idx_cashbox_operations_branch_id  ON cashbox_operations(branch_id);
CREATE INDEX IF NOT EXISTS idx_cashbox_operations_cashbox_id ON cashbox_operations(cashbox_id);
CREATE INDEX IF NOT EXISTS idx_cashbox_operations_date       ON cashbox_operations(operation_date);
