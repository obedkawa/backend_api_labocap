-- V23 — Compléter le schéma des contrats (story 5-1)

-- Champs manquants sur contrats
ALTER TABLE contrats
    ADD COLUMN IF NOT EXISTS name        VARCHAR(200),
    ADD COLUMN IF NOT EXISTS type        VARCHAR(50),
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS is_close    BOOLEAN NOT NULL DEFAULT FALSE;

-- Corriger le statut par défaut (INACTIF en accord avec Laravel)
ALTER TABLE contrats ALTER COLUMN status SET DEFAULT 'INACTIF';

-- Champs manquants sur details_contrats
ALTER TABLE details_contrats
    ADD COLUMN IF NOT EXISTS pourcentage         DECIMAL(5,2),
    ADD COLUMN IF NOT EXISTS amount_remise       DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS amount_after_remise DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS category_test_id    UUID REFERENCES category_tests(id);

-- Rendre lab_test_id et price nullable sur details_contrats (lignes par catégorie n'ont pas de test)
ALTER TABLE details_contrats ALTER COLUMN lab_test_id DROP NOT NULL;
ALTER TABLE details_contrats ALTER COLUMN price DROP NOT NULL;

-- Rendre patient_id nullable sur invoices (factures groupées par contrat sans patient direct)
ALTER TABLE invoices ALTER COLUMN patient_id DROP NOT NULL;

-- Index pour la vérification d'unicité par catégorie
CREATE INDEX IF NOT EXISTS idx_details_contrats_contrat_category
    ON details_contrats(contrat_id, category_test_id);

-- Permissions pour la gestion des contrats (si absentes)
INSERT INTO permissions (id, name, slug, created_at)
VALUES
    (gen_random_uuid(), 'Voir contrats',  'view-contracts',   NOW()),
    (gen_random_uuid(), 'Gérer contrats', 'manage-contracts', NOW())
ON CONFLICT (slug) DO NOTHING;
