-- ============================================================
-- V6 - Corrections schéma pour conformité avec labo-anapath-main (Laravel)
-- Source de vérité : migrations Laravel du projet labo-anapath-main
-- ============================================================

-- ------------------------------------------------------------
-- ROLES — Ajout colonnes audit + description + is_assignable
-- (Laravel: description text, is_assignable boolean, created_by FK, softDeletes)
-- ------------------------------------------------------------
ALTER TABLE roles
    ADD COLUMN IF NOT EXISTS description    TEXT,
    ADD COLUMN IF NOT EXISTS is_assignable  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS created_by     UUID,
    ADD COLUMN IF NOT EXISTS updated_by     UUID,
    ADD COLUMN IF NOT EXISTS deleted_at     TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_roles_deleted_at ON roles(deleted_at);

-- two_fas — schéma corrigé directement dans V1 (code + user_id, sans secret/is_enabled)

-- details__contrats — colonnes corrigées directement dans V1 (category_test_id + pourcentage)

-- ------------------------------------------------------------
-- CASHBOXES — Ajout opening_balance, current_balance
-- Laravel: opening_balance float, current_balance float, type enum
-- NOTE: branch_id et name sont des ajouts Spring (multi-tenant)
-- ------------------------------------------------------------
ALTER TABLE cashboxes
    ADD COLUMN IF NOT EXISTS opening_balance  DECIMAL(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS current_balance  DECIMAL(12,2) NOT NULL DEFAULT 0;

-- ------------------------------------------------------------
-- CASHBOX_ADDS — Correction structure
-- Laravel: id, date, amount, cheque_number, description, attachement, timestamps
-- La structure Spring était entièrement différente
-- ------------------------------------------------------------
DROP TABLE IF EXISTS cashbox_adds;
CREATE TABLE cashbox_adds (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID REFERENCES branches(id),
    cashbox_id      UUID REFERENCES cashboxes(id),
    date            DATE NOT NULL DEFAULT CURRENT_DATE,
    amount          DECIMAL(10,2) NOT NULL DEFAULT 0,
    cheque_number   BIGINT,
    description     TEXT,
    attachement     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_cashbox_adds_cashbox_id  ON cashbox_adds(cashbox_id);
CREATE INDEX idx_cashbox_adds_branch_id   ON cashbox_adds(branch_id);

-- ------------------------------------------------------------
-- EXPENSES — Alignement avec Laravel
-- Laravel: user_id, item_name, item_id, total_amount, unit_price, quantity,
--          supplier_id, expense_categorie_id (typo), receipt, cashbox_ticket_id,
--          paid int default 0, softDeletes, timestamps
--          + plus tard: date, invoice_number, payment enum
-- ------------------------------------------------------------
ALTER TABLE expenses
    ADD COLUMN IF NOT EXISTS item_name          VARCHAR(300),
    ADD COLUMN IF NOT EXISTS item_id            UUID REFERENCES articles(id),
    ADD COLUMN IF NOT EXISTS total_amount       DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS unit_price         DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS quantity           DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS supplier_id        UUID REFERENCES suppliers(id),
    ADD COLUMN IF NOT EXISTS receipt            VARCHAR(500),
    ADD COLUMN IF NOT EXISTS paid               INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS deleted_at         TIMESTAMP,
    ADD COLUMN IF NOT EXISTS date               DATE,
    ADD COLUMN IF NOT EXISTS invoice_number     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS payment            VARCHAR(20) DEFAULT 'ESPECES';

-- Renommer expense_category_id → expense_categorie_id (typo Laravel préservée)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'expenses' AND column_name = 'expense_category_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'expenses' AND column_name = 'expense_categorie_id'
    ) THEN
        ALTER TABLE expenses RENAME COLUMN expense_category_id TO expense_categorie_id;
    END IF;
END $$;

-- ------------------------------------------------------------
-- SIGNALS — Correction colonnes (type→type_signal, description→commentaire, +status)
-- Laravel: user_id, test_order_id, type_signal varchar, commentaire text, status boolean
-- ------------------------------------------------------------
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'signals' AND column_name = 'type'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'signals' AND column_name = 'type_signal'
    ) THEN
        ALTER TABLE signals RENAME COLUMN type TO type_signal;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'signals' AND column_name = 'description'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'signals' AND column_name = 'commentaire'
    ) THEN
        ALTER TABLE signals RENAME COLUMN description TO commentaire;
    END IF;
END $$;

ALTER TABLE signals
    ADD COLUMN IF NOT EXISTS status BOOLEAN;

-- ------------------------------------------------------------
-- TICKET_COMMENTS — Correction colonne (content→comment) + ajout read
-- Laravel: user_id, ticket_id, comment text, timestamps + read boolean
-- ------------------------------------------------------------
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'ticket_comments' AND column_name = 'content'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'ticket_comments' AND column_name = 'comment'
    ) THEN
        ALTER TABLE ticket_comments RENAME COLUMN content TO comment;
    END IF;
END $$;

ALTER TABLE ticket_comments
    ADD COLUMN IF NOT EXISTS read BOOLEAN NOT NULL DEFAULT FALSE;

-- ------------------------------------------------------------
-- TICKETS — Ajout ticket_code, is_resolved
-- Laravel: user_id, subject, description, ticket_code unique, is_resolved boolean
-- NOTE: Spring a title/status/priority qui ne sont pas dans Laravel
-- ------------------------------------------------------------
ALTER TABLE tickets
    ADD COLUMN IF NOT EXISTS ticket_code  VARCHAR(100) UNIQUE,
    ADD COLUMN IF NOT EXISTS is_resolved  BOOLEAN NOT NULL DEFAULT FALSE;

-- ------------------------------------------------------------
-- ASSIGNMENT_DOCTORS — Ajout colonne comment
-- Laravel: doctor_id FK users, report_id FK reports, comment text
-- ------------------------------------------------------------
ALTER TABLE assignment_doctors
    ADD COLUMN IF NOT EXISTS comment TEXT;

-- ------------------------------------------------------------
-- APPEL_BY_REPORTS — Ajout colonne appel_id
-- Laravel: report_id FK title_reports, appel_id varchar, timestamps
-- ------------------------------------------------------------
ALTER TABLE appel_by_reports
    ADD COLUMN IF NOT EXISTS appel_id VARCHAR(255);

-- ------------------------------------------------------------
-- DOCS — Ajout attachment, is_current_version, file_size
-- Laravel: title, attachment, is_current_version bool, documentation_categorie_id
-- ------------------------------------------------------------
-- Renommer documentation_category_id → documentation_categorie_id (typo Laravel)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'docs' AND column_name = 'documentation_category_id'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'docs' AND column_name = 'documentation_categorie_id'
    ) THEN
        ALTER TABLE docs RENAME COLUMN documentation_category_id TO documentation_categorie_id;
    END IF;
END $$;

ALTER TABLE docs
    ADD COLUMN IF NOT EXISTS attachment         VARCHAR(500),
    ADD COLUMN IF NOT EXISTS is_current_version BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS file_size          BIGINT;

-- ------------------------------------------------------------
-- DOC_VERSIONS — Ajout attachment, file_size, title
-- Laravel: doc_id FK, attachment, softDeletes + version, role_id, user_id, file_size, title
-- ------------------------------------------------------------
ALTER TABLE doc_versions
    ADD COLUMN IF NOT EXISTS attachment VARCHAR(500),
    ADD COLUMN IF NOT EXISTS file_size  BIGINT,
    ADD COLUMN IF NOT EXISTS title      VARCHAR(300);
