-- V27 — Story 5-5 : Remboursements et avoirs — refonte schéma

-- 1. Colonnes manquantes sur refund_reasons pour AuditableEntity
ALTER TABLE refund_reasons
    ADD COLUMN IF NOT EXISTS created_by UUID,
    ADD COLUMN IF NOT EXISTS updated_by UUID,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- 2. Colonnes manquantes sur refund_request_logs
ALTER TABLE refund_request_logs
    ADD COLUMN IF NOT EXISTS operation  VARCHAR(50),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();
-- Rendre action nullable pour compat avec les nouveaux inserts (operation devient la source)
ALTER TABLE refund_request_logs ALTER COLUMN action DROP NOT NULL;
-- Synchroniser action existant → operation
UPDATE refund_request_logs SET operation = LEFT(action, 50) WHERE operation IS NULL;

-- 3. Refonte de refund_requests : remplacer payment_id par invoice_id + nouveaux champs
ALTER TABLE refund_requests
    ADD COLUMN IF NOT EXISTS invoice_id       UUID REFERENCES invoices(id),
    ADD COLUMN IF NOT EXISTS refund_reason_id UUID REFERENCES refund_reasons(id),
    ADD COLUMN IF NOT EXISTS montant          DECIMAL(10,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS note             TEXT,
    ADD COLUMN IF NOT EXISTS attachment       VARCHAR(500),
    ADD COLUMN IF NOT EXISTS code             VARCHAR(50),
    ADD COLUMN IF NOT EXISTS deleted_at       TIMESTAMP;

ALTER TABLE refund_requests DROP COLUMN IF EXISTS payment_id;
ALTER TABLE refund_requests DROP COLUMN IF EXISTS reason;
ALTER TABLE refund_requests DROP COLUMN IF EXISTS amount;

-- 4. Changer le statut par défaut pour compatibilité Laravel ("En attente" avec un seul 'p')
ALTER TABLE refund_requests ALTER COLUMN status DROP NOT NULL;
ALTER TABLE refund_requests ALTER COLUMN status SET DEFAULT 'En attente';
UPDATE refund_requests SET status = 'En attente' WHERE status = 'PENDING' OR status IS NULL;
ALTER TABLE refund_requests ALTER COLUMN status SET NOT NULL;

-- 5. Unicité par facture (une seule demande de remboursement par facture)
ALTER TABLE refund_requests
    DROP CONSTRAINT IF EXISTS refund_requests_invoice_id_unique;
ALTER TABLE refund_requests
    ADD CONSTRAINT refund_requests_invoice_id_unique UNIQUE (invoice_id);

CREATE INDEX IF NOT EXISTS idx_refund_requests_invoice_id ON refund_requests(invoice_id);
