-- V33 — Table des catégories de fournisseurs + FK dans suppliers

CREATE TABLE supplier_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID,
    deleted_at  TIMESTAMP
);

CREATE INDEX idx_supplier_categories_branch_id  ON supplier_categories(branch_id);
CREATE INDEX idx_supplier_categories_deleted_at ON supplier_categories(deleted_at);

ALTER TABLE suppliers
    ADD COLUMN IF NOT EXISTS supplier_category_id UUID REFERENCES supplier_categories(id);
