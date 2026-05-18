-- Bons de caisse (cashbox_vouchers) — distinct from cashbox_tickets (mouvement unitaire)
CREATE TABLE cashbox_vouchers (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id            UUID NOT NULL,
    cashbox_id           UUID REFERENCES cashboxes(id),
    code                 VARCHAR(50),
    amount               DECIMAL(12,2) NOT NULL DEFAULT 0,
    description          TEXT,
    status               VARCHAR(20) NOT NULL DEFAULT 'en attente',
    supplier_id          UUID,
    expense_category_id  UUID,
    ticket_file          VARCHAR(500),
    deleted_at           TIMESTAMP,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by           UUID,
    updated_by           UUID
);

CREATE TABLE cashbox_voucher_details (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id          UUID NOT NULL,
    cashbox_voucher_id UUID NOT NULL REFERENCES cashbox_vouchers(id),
    item_name          VARCHAR(200),
    item_id            UUID,
    quantity           DECIMAL(10,2),
    unit_price         DECIMAL(12,2),
    line_amount        DECIMAL(12,2),
    deleted_at         TIMESTAMP,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by         UUID,
    updated_by         UUID
);

-- Catégories de dépenses
CREATE TABLE expense_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    deleted_at  TIMESTAMP,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID
);

-- Dépenses
CREATE TABLE expenses (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id             UUID NOT NULL,
    amount                DECIMAL(12,2) NOT NULL DEFAULT 0,
    description           TEXT,
    item_name             VARCHAR(200),
    item_id               UUID,
    total_amount          DECIMAL(12,2),
    unit_price            DECIMAL(12,2),
    quantity              DECIMAL(10,2),
    supplier_id           UUID,
    expense_categorie_id  UUID REFERENCES expense_categories(id),
    receipt               VARCHAR(500),
    cashbox_voucher_id    UUID REFERENCES cashbox_vouchers(id),
    paid                  INTEGER NOT NULL DEFAULT 0,
    date                  DATE,
    invoice_number        VARCHAR(100),
    payment               VARCHAR(20),
    deleted_at            TIMESTAMP,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by            UUID,
    updated_by            UUID
);

-- ATTENTION: nom de table OBLIGATOIREMENT "expence_details" (avec 'c', pas 's')
-- Typo issu du code Laravel — à préserver absolument pour la cohérence métier
CREATE TABLE expence_details (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id    UUID NOT NULL,
    expense_id   UUID NOT NULL REFERENCES expenses(id),
    article_name VARCHAR(200),
    article_id   UUID,
    quantity     DECIMAL(10,2),
    unit_price   DECIMAL(12,2),
    line_amount  DECIMAL(12,2),
    deleted_at   TIMESTAMP,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by   UUID,
    updated_by   UUID
);

CREATE INDEX idx_cashbox_vouchers_branch         ON cashbox_vouchers(branch_id);
CREATE INDEX idx_cashbox_voucher_details_voucher  ON cashbox_voucher_details(cashbox_voucher_id);
CREATE INDEX idx_expense_categories_branch       ON expense_categories(branch_id);
CREATE INDEX idx_expenses_branch_id              ON expenses(branch_id);
CREATE INDEX idx_expence_details_expense_id      ON expence_details(expense_id);
