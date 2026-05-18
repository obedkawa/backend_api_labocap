-- ============================================================
-- V7 - Corrections schéma : conformité 100% avec labo-anapath-main (Laravel)
-- Champs renommés, colonnes manquantes ajoutées, colonnes fictives supprimées
-- Source de vérité : migrations Laravel du projet labo-anapath-main
-- ============================================================

-- ============================================================
-- PATIENTS — renommage colonnes + ajout champs Laravel
-- Laravel: code unique, firstname, lastname, genre (string), telephone1, telephone2,
--          adresse, age, year_or_month, birthday, profession, langue, email
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='first_name')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='firstname') THEN
        ALTER TABLE patients RENAME COLUMN first_name TO firstname;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='last_name')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='lastname') THEN
        ALTER TABLE patients RENAME COLUMN last_name TO lastname;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='phone')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='telephone1') THEN
        ALTER TABLE patients RENAME COLUMN phone TO telephone1;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='address')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='adresse') THEN
        ALTER TABLE patients RENAME COLUMN address TO adresse;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='gender')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='genre') THEN
        ALTER TABLE patients RENAME COLUMN gender TO genre;
        ALTER TABLE patients ALTER COLUMN genre TYPE VARCHAR(20) USING genre::text;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='birth_date')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='patients' AND column_name='birthday') THEN
        ALTER TABLE patients RENAME COLUMN birth_date TO birthday;
    END IF;
END $$;

-- Drop old phone-based unique index (column renamed to telephone1)
DROP INDEX IF EXISTS idx_patients_phone_branch;
DROP INDEX IF EXISTS idx_patients_phone;

-- Add missing columns
ALTER TABLE patients
    ADD COLUMN IF NOT EXISTS code         VARCHAR(100),
    ADD COLUMN IF NOT EXISTS telephone2   VARCHAR(20),
    ADD COLUMN IF NOT EXISTS age          INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS year_or_month BOOLEAN,
    ADD COLUMN IF NOT EXISTS profession   VARCHAR(200),
    ADD COLUMN IF NOT EXISTS langue       VARCHAR(20);

-- Re-create indexes with new column names
CREATE INDEX IF NOT EXISTS idx_patients_telephone1 ON patients(telephone1);
CREATE UNIQUE INDEX IF NOT EXISTS idx_patients_code_branch ON patients(code, branch_id) WHERE code IS NOT NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_patients_telephone1_branch ON patients(telephone1, branch_id) WHERE telephone1 IS NOT NULL AND deleted_at IS NULL;

-- ============================================================
-- HOSPITALS — renommage colonnes + ajout email/commission + suppression contract_permission
-- Laravel: name (unique), adresse, email, telephone, commission
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='hospitals' AND column_name='phone')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='hospitals' AND column_name='telephone') THEN
        ALTER TABLE hospitals RENAME COLUMN phone TO telephone;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='hospitals' AND column_name='address')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='hospitals' AND column_name='adresse') THEN
        ALTER TABLE hospitals RENAME COLUMN address TO adresse;
    END IF;
END $$;

ALTER TABLE hospitals
    ADD COLUMN IF NOT EXISTS email       VARCHAR(100),
    ADD COLUMN IF NOT EXISTS commission  DECIMAL(10,2);

ALTER TABLE hospitals
    DROP COLUMN IF EXISTS contract_permission;

CREATE UNIQUE INDEX IF NOT EXISTS idx_hospitals_name_branch ON hospitals(name, branch_id) WHERE deleted_at IS NULL;

-- ============================================================
-- DOCTORS — conformité Laravel : name unique (pas first_name/last_name),
--           telephone, role, commission ; suppression speciality et hospital_id
-- Laravel: name unique, email, role, telephone, commission
-- ============================================================

-- 1. Ajouter la colonne name avec valeur par défaut depuis first_name + last_name
ALTER TABLE doctors
    ADD COLUMN IF NOT EXISTS name VARCHAR(200);

-- 2. Backfill name depuis first_name + last_name existants
UPDATE doctors
SET name = COALESCE(TRIM(COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')), first_name, last_name, 'Médecin inconnu')
WHERE name IS NULL;

-- 3. Renommer phone → telephone
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='doctors' AND column_name='phone')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='doctors' AND column_name='telephone') THEN
        ALTER TABLE doctors RENAME COLUMN phone TO telephone;
    END IF;
END $$;

-- 4. Ajouter role et commission
ALTER TABLE doctors
    ADD COLUMN IF NOT EXISTS role        VARCHAR(100),
    ADD COLUMN IF NOT EXISTS commission  DECIMAL(10,2) NOT NULL DEFAULT 0;

-- 5. Supprimer la FK hospital_id (dépendance vers hospitals — pas dans Laravel)
DO $$
DECLARE
    fk_name TEXT;
BEGIN
    SELECT constraint_name INTO fk_name
    FROM information_schema.table_constraints
    WHERE table_name = 'doctors'
      AND constraint_type = 'FOREIGN KEY'
      AND constraint_name LIKE '%hospital%';
    IF fk_name IS NOT NULL THEN
        EXECUTE 'ALTER TABLE doctors DROP CONSTRAINT ' || fk_name;
    END IF;
END $$;

ALTER TABLE doctors
    DROP COLUMN IF EXISTS hospital_id,
    DROP COLUMN IF EXISTS speciality,
    DROP COLUMN IF EXISTS first_name,
    DROP COLUMN IF EXISTS last_name;

CREATE UNIQUE INDEX IF NOT EXISTS idx_doctors_name_branch ON doctors(name, branch_id) WHERE deleted_at IS NULL;

-- ============================================================
-- BRANCHES — ajout code/location, suppression address/phone
-- Laravel: name, code (nullable), location (nullable), softDeletes
-- ============================================================
ALTER TABLE branches
    ADD COLUMN IF NOT EXISTS code     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS location VARCHAR(200);

ALTER TABLE branches
    DROP COLUMN IF EXISTS address,
    DROP COLUMN IF EXISTS phone;

-- ============================================================
-- USERS — branch_id nullable (Laravel: nullable FK)
-- ============================================================
ALTER TABLE users
    ALTER COLUMN branch_id DROP NOT NULL;

-- ============================================================
-- BRANCH_USER — ajout is_default
-- Laravel: user_id, branch_id, is_default boolean default true
-- ============================================================
ALTER TABLE branch_user
    ADD COLUMN IF NOT EXISTS is_default BOOLEAN NOT NULL DEFAULT TRUE;

-- ============================================================
-- ROLES — name et slug peuvent être NULL (Laravel: nullable unique)
-- ============================================================
DO $$
BEGIN
    BEGIN
        ALTER TABLE roles ALTER COLUMN name DROP NOT NULL;
    EXCEPTION WHEN others THEN NULL;
    END;
    BEGIN
        ALTER TABLE roles ALTER COLUMN slug DROP NOT NULL;
    EXCEPTION WHEN others THEN NULL;
    END;
END $$;

-- ============================================================
-- ASSIGNMENT_DOCTORS — renommer user_id → doctor_id
-- Laravel: doctor_id FK users, report_id FK reports
-- ============================================================
DO $$
DECLARE
    fk_name TEXT;
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='assignment_doctors' AND column_name='user_id')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='assignment_doctors' AND column_name='doctor_id') THEN

        SELECT constraint_name INTO fk_name
        FROM information_schema.table_constraints
        WHERE table_name = 'assignment_doctors'
          AND constraint_type = 'FOREIGN KEY'
          AND constraint_name LIKE '%user%';
        IF fk_name IS NOT NULL THEN
            EXECUTE 'ALTER TABLE assignment_doctors DROP CONSTRAINT ' || fk_name;
        END IF;

        ALTER TABLE assignment_doctors RENAME COLUMN user_id TO doctor_id;

        ALTER TABLE assignment_doctors
            ADD CONSTRAINT fk_assignment_doctors_doctor_id
            FOREIGN KEY (doctor_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE RESTRICT;
    END IF;
END $$;

-- ============================================================
-- BANKS — ajout account_number et description
-- Laravel: name, account_number, description, timestamps
-- ============================================================
ALTER TABLE banks
    ADD COLUMN IF NOT EXISTS account_number VARCHAR(255),
    ADD COLUMN IF NOT EXISTS description    TEXT;
