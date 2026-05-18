-- V13 — Colonnes manquantes pour la validation des bons d'examen (Story 3-2)
-- Source de vérité : labo-anapath-main (Laravel)

-- contrats : facturation groupée vs individuelle
-- invoice_unique=true  → une facture partagée pour tout le contrat
-- invoice_unique=false → une facture par bon d'examen
ALTER TABLE contrats
    ADD COLUMN IF NOT EXISTS invoice_unique BOOLEAN NOT NULL DEFAULT TRUE;

-- invoices : alignement avec la table Laravel invoices
ALTER TABLE invoices
    ADD COLUMN IF NOT EXISTS code          VARCHAR(50),
    ADD COLUMN IF NOT EXISTS contrat_id    UUID REFERENCES contrats(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS client_name   VARCHAR(100),
    ADD COLUMN IF NOT EXISTS client_address TEXT,
    ADD COLUMN IF NOT EXISTS subtotal      FLOAT,
    ADD COLUMN IF NOT EXISTS discount      FLOAT,
    ADD COLUMN IF NOT EXISTS paid          BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_invoices_contrat_id ON invoices(contrat_id);

-- invoice_details : alignement avec la table Laravel invoice_details
ALTER TABLE invoice_details
    ADD COLUMN IF NOT EXISTS test_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS price     FLOAT,
    ADD COLUMN IF NOT EXISTS discount  FLOAT;

-- reports : code unique + description (utilisés à la validation)
ALTER TABLE reports
    ADD COLUMN IF NOT EXISTS code        VARCHAR(100),
    ADD COLUMN IF NOT EXISTS description TEXT;

CREATE UNIQUE INDEX IF NOT EXISTS idx_reports_code ON reports(code) WHERE code IS NOT NULL;
