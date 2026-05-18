-- Ajout des champs workflow sur test_pathology_macros
ALTER TABLE test_pathology_macros
    ADD COLUMN IF NOT EXISTS test_order_id          UUID REFERENCES test_orders(id),
    ADD COLUMN IF NOT EXISTS circulation            BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS embedding              BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS microtomy_spreading    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS staining               BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS mounting               BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS macro_date             DATE,
    ADD COLUMN IF NOT EXISTS created_by             UUID,
    ADD COLUMN IF NOT EXISTS updated_by             UUID,
    ADD COLUMN IF NOT EXISTS deleted_at             TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_macros_test_order ON test_pathology_macros(test_order_id);

-- Correction test_order_assignments : test_order_id devient nullable + colonnes manquantes
ALTER TABLE test_order_assignments
    ALTER COLUMN test_order_id DROP NOT NULL,
    ADD COLUMN IF NOT EXISTS date       DATE,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS created_by UUID,
    ADD COLUMN IF NOT EXISTS updated_by UUID;

-- Restructuration test_order_assignment_details (drop + recreate)
DROP TABLE IF EXISTS test_order_assignment_details CASCADE;

CREATE TABLE test_order_assignment_details (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id                   UUID NOT NULL,
    test_order_assignment_id    UUID NOT NULL REFERENCES test_order_assignments(id) ON DELETE CASCADE,
    test_order_id               UUID REFERENCES test_orders(id),
    test_order_code             VARCHAR(50),
    note                        TEXT,
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by                  UUID,
    updated_by                  UUID
);

CREATE INDEX IF NOT EXISTS idx_toad_assignment ON test_order_assignment_details(test_order_assignment_id);
CREATE INDEX IF NOT EXISTS idx_toad_test_order ON test_order_assignment_details(test_order_id);
