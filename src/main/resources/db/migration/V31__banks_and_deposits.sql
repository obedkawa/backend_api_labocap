-- Banques référencées dans le système
CREATE TABLE banks (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id      UUID NOT NULL,
    name           VARCHAR(200) NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    description    TEXT,
    deleted_at     TIMESTAMP,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by     UUID,
    updated_by     UUID
);

CREATE INDEX idx_banks_branch_id ON banks(branch_id);

-- Dépôts bancaires: transfert caisse vente → banque
-- Chaque dépôt génère une CashboxOperation DEBIT sur la caisse vente
CREATE TABLE bank_deposits (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    bank_id     UUID NOT NULL REFERENCES banks(id),
    cashbox_id  UUID NOT NULL REFERENCES cashboxes(id),
    amount      DECIMAL(12,2) NOT NULL,
    date        DATE NOT NULL,
    description TEXT,
    attachement VARCHAR(500),
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID
);

CREATE INDEX idx_bank_deposits_branch_id ON bank_deposits(branch_id);
CREATE INDEX idx_bank_deposits_bank_id   ON bank_deposits(bank_id);
CREATE INDEX idx_bank_deposits_date      ON bank_deposits(date);
