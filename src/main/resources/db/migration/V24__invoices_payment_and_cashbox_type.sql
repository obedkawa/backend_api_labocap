-- V24 — Champs paiement/MECeF sur invoices + type sur cashboxes (story 5-2)

ALTER TABLE invoices
    ADD COLUMN IF NOT EXISTS code_mecef     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS counters       TEXT,
    ADD COLUMN IF NOT EXISTS date_generate  VARCHAR(50),
    ADD COLUMN IF NOT EXISTS nim            VARCHAR(100),
    ADD COLUMN IF NOT EXISTS qrcode         TEXT,
    ADD COLUMN IF NOT EXISTS payment        VARCHAR(30),
    ADD COLUMN IF NOT EXISTS status_invoice INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS reference      UUID REFERENCES invoices(id);

ALTER TABLE cashboxes ADD COLUMN IF NOT EXISTS type VARCHAR(20) NOT NULL DEFAULT 'vente';

CREATE INDEX IF NOT EXISTS idx_invoices_paid            ON invoices(paid, branch_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status_invoice  ON invoices(status_invoice);
CREATE INDEX IF NOT EXISTS idx_invoices_contrat_id      ON invoices(contrat_id);
