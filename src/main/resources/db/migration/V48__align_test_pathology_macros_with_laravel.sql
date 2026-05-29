-- V48: Alignement test_pathology_macros avec le schéma Laravel MySQL
-- Source de vérité : labo-anapath-main (Laravel)
-- Nécessaire pour la migration future MySQL → PostgreSQL

-- 1. Colonne observation (LONGTEXT Laravel → TEXT PostgreSQL)
ALTER TABLE test_pathology_macros ADD COLUMN IF NOT EXISTS observation TEXT;

-- 2. Rendre title NULLABLE (absent dans Laravel — ne doit pas bloquer la migration de données)
ALTER TABLE test_pathology_macros ALTER COLUMN title DROP NOT NULL;

-- 3. Valeurs par défaut FALSE sur les étapes (migration Laravel : default(false))
ALTER TABLE test_pathology_macros ALTER COLUMN circulation SET DEFAULT false;
ALTER TABLE test_pathology_macros ALTER COLUMN embedding SET DEFAULT false;
ALTER TABLE test_pathology_macros ALTER COLUMN microtomy_spreading SET DEFAULT false;
ALTER TABLE test_pathology_macros ALTER COLUMN staining SET DEFAULT false;
ALTER TABLE test_pathology_macros ALTER COLUMN mounting SET DEFAULT false;

-- 4. Contrainte UNIQUE sur test_order_id (Laravel : index UNIQUE sur id_test_pathology_order)
-- Un seul enregistrement macroscopie par bon d'examen
ALTER TABLE test_pathology_macros
    ADD CONSTRAINT uq_macro_test_order UNIQUE (test_order_id);

-- 5. Aligner users_permissions : ajouter updated_at (présent dans Laravel)
ALTER TABLE users_permissions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();
