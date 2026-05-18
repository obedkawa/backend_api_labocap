-- Enrichir cashbox_dailies avec les champs de décompte par mode de paiement
-- et les colonnes AuditableEntity manquantes
ALTER TABLE cashbox_dailies
    ADD COLUMN IF NOT EXISTS cash_calculated             DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS cash_confirmation           DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS cash_ecart                  DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS mobile_money_calculated     DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS mobile_money_confirmation   DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS mobile_money_ecart          DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS cheque_calculated           DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS cheque_confirmation         DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS cheque_ecart                DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS virement_calculated         DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS virement_confirmation       DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS virement_ecart              DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS total_calculated            DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS total_confirmation          DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS total_ecart                 DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS status                      INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS code                        VARCHAR(50),
    ADD COLUMN IF NOT EXISTS deleted_at                  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS created_by                  UUID,
    ADD COLUMN IF NOT EXISTS updated_by                  UUID;

-- Ajouter paymentMethod sur cashbox_operations pour faciliter l'agrégation du résumé journalier
ALTER TABLE cashbox_operations
    ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20);

-- Ajouter opening_balance et statut sur cashboxes (utilisés par la clôture journalière)
ALTER TABLE cashboxes
    ADD COLUMN IF NOT EXISTS opening_balance DECIMAL(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS statut          INTEGER NOT NULL DEFAULT 0;
