-- V9: corrections catalogue d'analyses (conformité Laravel)

-- 1. Ajouter 'code' sur category_tests
ALTER TABLE category_tests
    ADD COLUMN IF NOT EXISTS code VARCHAR(50);

-- 2. Ajouter 'slug' sur type_orders (identifiant métier critique pour Epic 3+)
ALTER TABLE type_orders
    ADD COLUMN IF NOT EXISTS slug VARCHAR(100);
CREATE UNIQUE INDEX IF NOT EXISTS idx_type_orders_slug ON type_orders(slug)
    WHERE slug IS NOT NULL;

-- 3. Ajouter 'status' sur lab_tests
ALTER TABLE lab_tests
    ADD COLUMN IF NOT EXISTS status VARCHAR(10) NOT NULL DEFAULT 'ACTIF';
