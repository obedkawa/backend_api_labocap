-- V14 — Corrections post-review story 3-2

-- P7: Garantir invoice_unique non-NULL pour contrats existants antérieurs à V13
-- DEFAULT TRUE est défini en V13 mais ne backfille pas les lignes existantes
UPDATE contrats SET invoice_unique = TRUE WHERE invoice_unique IS NULL;

-- D1: Rendre test_order_id nullable pour les factures de contrat groupé
-- Les factures groupées (invoice_unique=true) sont partagées par contrat,
-- pas liées à un bon spécifique. La création initiale est gérée par la story 5-2.
ALTER TABLE invoices ALTER COLUMN test_order_id DROP NOT NULL;
