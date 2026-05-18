-- V12: Alignement du schéma test_orders/detail_test_orders sur la source de vérité Laravel
-- - code nullable (généré uniquement lors de la validation, Story 3-2)
-- - champs manquants : reference_hopital, is_urgent, archive, subtotal, discount, total,
--   attribuate_doctor_id, assigned_to_user_id, test_affiliate, files_name, option, status_appel, assignment_date
-- - suppression de is_called/called_at (absents dans Laravel test_orders)
-- - detail_test_orders : alignement financier (test_name, price, discount, total)

-- ============================================================
-- test_orders
-- ============================================================

-- code doit être nullable (assigné uniquement à la validation)
ALTER TABLE test_orders ALTER COLUMN code DROP NOT NULL;

-- champs manquants
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS reference_hopital VARCHAR(255);
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS is_urgent BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS archive VARCHAR(255);
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS subtotal FLOAT;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS discount FLOAT;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS total FLOAT;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS attribuate_doctor_id UUID REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS assigned_to_user_id UUID REFERENCES users(id) ON DELETE RESTRICT;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS test_affiliate TEXT;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS files_name TEXT;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS option BOOLEAN;
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS status_appel VARCHAR(255);
ALTER TABLE test_orders ADD COLUMN IF NOT EXISTS assignment_date TIMESTAMP;

-- suppression des champs incorrects (absents dans Laravel test_orders)
ALTER TABLE test_orders DROP COLUMN IF EXISTS is_called;
ALTER TABLE test_orders DROP COLUMN IF EXISTS called_at;

-- ============================================================
-- detail_test_orders : alignement financier avec Laravel
-- ============================================================

-- supprimer champs cliniques incorrects (appartiennent aux rapports)
ALTER TABLE detail_test_orders DROP COLUMN IF EXISTS result;
ALTER TABLE detail_test_orders DROP COLUMN IF EXISTS normal_value;

-- ajouter champs financiers conformes Laravel
ALTER TABLE detail_test_orders ADD COLUMN IF NOT EXISTS test_name VARCHAR(300) NOT NULL DEFAULT '';
ALTER TABLE detail_test_orders ADD COLUMN IF NOT EXISTS price FLOAT NOT NULL DEFAULT 0;
ALTER TABLE detail_test_orders ADD COLUMN IF NOT EXISTS discount FLOAT DEFAULT 0;
ALTER TABLE detail_test_orders ADD COLUMN IF NOT EXISTS total FLOAT NOT NULL DEFAULT 0;
ALTER TABLE detail_test_orders ADD COLUMN IF NOT EXISTS status BOOLEAN;
