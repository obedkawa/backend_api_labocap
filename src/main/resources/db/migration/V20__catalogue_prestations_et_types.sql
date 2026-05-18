-- V20: catalogue prestations, types de consultation (corrections + nouvelles tables)

-- 1. Corrections sur type_consultations
ALTER TABLE type_consultations
    ADD COLUMN IF NOT EXISTS slug        VARCHAR(200),
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_type_consultations_slug_branch
    ON type_consultations(slug, branch_id) WHERE deleted_at IS NULL;

-- 2. category_prestations
CREATE TABLE IF NOT EXISTS category_prestations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    slug        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX IF NOT EXISTS idx_category_prestations_branch_id
    ON category_prestations(branch_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_category_prestations_slug_branch
    ON category_prestations(slug, branch_id) WHERE deleted_at IS NULL;

-- 3. prestations
CREATE TABLE IF NOT EXISTS prestations (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id               UUID NOT NULL REFERENCES branches(id),
    name                    VARCHAR(300) NOT NULL,
    price                   DECIMAL(10,2) NOT NULL DEFAULT 0,
    description             TEXT,
    category_prestation_id  UUID NOT NULL REFERENCES category_prestations(id),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at              TIMESTAMP,
    created_by              UUID,
    updated_by              UUID
);
CREATE INDEX IF NOT EXISTS idx_prestations_branch_id    ON prestations(branch_id);
CREATE INDEX IF NOT EXISTS idx_prestations_category_id  ON prestations(category_prestation_id);

-- 4. prestation_orders
CREATE TABLE IF NOT EXISTS prestation_orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NOT NULL REFERENCES branches(id),
    patient_id      UUID NOT NULL REFERENCES patients(id),
    prestation_id   UUID NOT NULL REFERENCES prestations(id),
    total           DECIMAL(10,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'Nouveau',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    created_by      UUID,
    updated_by      UUID
);
CREATE INDEX IF NOT EXISTS idx_prestation_orders_branch_id   ON prestation_orders(branch_id);
CREATE INDEX IF NOT EXISTS idx_prestation_orders_patient_id  ON prestation_orders(patient_id);
CREATE INDEX IF NOT EXISTS idx_prestation_orders_prestation  ON prestation_orders(prestation_id);

-- 5. Permissions
INSERT INTO permissions (id, name, slug, created_at) VALUES
    (gen_random_uuid(), 'Voir catégories prestations',    'view-category-prestations',   NOW()),
    (gen_random_uuid(), 'Créer catégorie prestation',     'create-category-prestations', NOW()),
    (gen_random_uuid(), 'Modifier catégorie prestation',  'edit-category-prestations',   NOW()),
    (gen_random_uuid(), 'Supprimer catégorie prestation', 'delete-category-prestations', NOW()),
    (gen_random_uuid(), 'Voir prestations',               'view-prestations',            NOW()),
    (gen_random_uuid(), 'Créer prestation',               'create-prestations',          NOW()),
    (gen_random_uuid(), 'Modifier prestation',            'edit-prestations',            NOW()),
    (gen_random_uuid(), 'Supprimer prestation',           'delete-prestations',          NOW()),
    (gen_random_uuid(), 'Voir orders prestations',        'view-prestation-orders',      NOW()),
    (gen_random_uuid(), 'Créer order prestation',         'create-prestation-orders',    NOW()),
    (gen_random_uuid(), 'Modifier order prestation',      'edit-prestation-orders',      NOW()),
    (gen_random_uuid(), 'Supprimer order prestation',     'delete-prestation-orders',    NOW()),
    (gen_random_uuid(), 'Créer type consultation',        'create-type-consultations',   NOW()),
    (gen_random_uuid(), 'Modifier type consultation',     'edit-type-consultations',     NOW()),
    (gen_random_uuid(), 'Supprimer type consultation',    'delete-type-consultations',   NOW())
ON CONFLICT (slug) DO NOTHING;
