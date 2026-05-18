-- ============================================================
-- V3 - Tables manquantes + corrections orthographe
-- ============================================================

-- ------------------------------------------------------------
-- Renommages critiques (typos Laravel préservées)
-- ------------------------------------------------------------
ALTER TABLE details_contrats RENAME TO details__contrats;
ALTER TABLE title_reports    RENAME TO report_titles;

-- Mise à jour des noms d'index (clarté, pas obligatoire)
ALTER INDEX IF EXISTS idx_details_contrats_contrat_id RENAME TO idx_details__contrats_contrat_id;
ALTER INDEX IF EXISTS idx_title_reports_branch_id     RENAME TO idx_report_titles_branch_id;

-- ------------------------------------------------------------
-- Correction cashboxes : ajout colonne type (findByType rule)
-- ------------------------------------------------------------
ALTER TABLE cashboxes ADD COLUMN IF NOT EXISTS type VARCHAR(50);

-- ============================================================
-- GROUPE 1 — Tables sans dépendances externes
-- ============================================================

-- ------------------------------------------------------------
-- personal_access_tokens
-- ------------------------------------------------------------
CREATE TABLE personal_access_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tokenable_type  VARCHAR(255) NOT NULL,
    tokenable_id    UUID NOT NULL,
    name            VARCHAR(255) NOT NULL,
    token           VARCHAR(64) NOT NULL UNIQUE,
    abilities       TEXT NULL,
    last_used_at    TIMESTAMP NULL,
    expires_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_personal_access_tokens_tokenable ON personal_access_tokens(tokenable_type, tokenable_id);

-- ------------------------------------------------------------
-- password_resets
-- ------------------------------------------------------------
CREATE TABLE password_resets (
    email       VARCHAR(150) NOT NULL,
    token       VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_password_resets_email ON password_resets(email);

-- ------------------------------------------------------------
-- branch_user (pivot multi-branch)
-- ------------------------------------------------------------
CREATE TABLE branch_user (
    branch_id  UUID NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    user_id    UUID NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    PRIMARY KEY (branch_id, user_id)
);

-- clients — créé dans V5 (version complète avec colonnes audit)

-- ------------------------------------------------------------
-- data_codes (catalogue codes de données)
-- ------------------------------------------------------------
CREATE TABLE data_codes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    code        VARCHAR(50) NOT NULL,
    label       VARCHAR(255) NOT NULL,
    type        VARCHAR(100) NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_data_codes_branch_id ON data_codes(branch_id);
CREATE INDEX idx_data_codes_type      ON data_codes(type);

-- ------------------------------------------------------------
-- ressources (RBAC)
-- ------------------------------------------------------------
CREATE TABLE ressources (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150) NOT NULL,
    slug        VARCHAR(150) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ressources_slug ON ressources(slug);

-- ------------------------------------------------------------
-- operations (RBAC)
-- ------------------------------------------------------------
CREATE TABLE operations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150) NOT NULL,
    slug        VARCHAR(150) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_operations_slug ON operations(slug);

-- ------------------------------------------------------------
-- banks
-- ------------------------------------------------------------
CREATE TABLE banks (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    code        VARCHAR(50) NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_banks_branch_id ON banks(branch_id);

-- ------------------------------------------------------------
-- expense_categories
-- ------------------------------------------------------------
CREATE TABLE expense_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_expense_categories_branch_id ON expense_categories(branch_id);

-- ------------------------------------------------------------
-- refund_reasons
-- ------------------------------------------------------------
CREATE TABLE refund_reasons (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    label       VARCHAR(300) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_refund_reasons_branch_id ON refund_reasons(branch_id);

-- ------------------------------------------------------------
-- setting_invoices (config MECeF + facturation)
-- ------------------------------------------------------------
CREATE TABLE setting_invoices (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    key         VARCHAR(100) NOT NULL,
    value       TEXT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_setting_invoices_branch_id ON setting_invoices(branch_id);
CREATE UNIQUE INDEX idx_setting_invoices_key_branch ON setting_invoices(key, branch_id);

-- ------------------------------------------------------------
-- category_prestations
-- ------------------------------------------------------------
CREATE TABLE category_prestations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_category_prestations_branch_id ON category_prestations(branch_id);

-- ------------------------------------------------------------
-- type_consultation_files
-- ------------------------------------------------------------
CREATE TABLE type_consultation_files (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_type_consultation_files_branch_id ON type_consultation_files(branch_id);

-- ------------------------------------------------------------
-- appel_test_oders (TYPO PRESERVEE — sans 'r' final)
-- ------------------------------------------------------------
CREATE TABLE appel_test_oders (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NULL REFERENCES branches(id),
    type        VARCHAR(255) NULL,
    account_id  VARCHAR(255) NULL,
    voice_id    VARCHAR(255) NULL,
    event       VARCHAR(255) NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_appel_test_oders_branch_id ON appel_test_oders(branch_id);

-- ------------------------------------------------------------
-- supplier_categories
-- ------------------------------------------------------------
CREATE TABLE supplier_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_supplier_categories_branch_id ON supplier_categories(branch_id);

-- ------------------------------------------------------------
-- documentation_categories
-- ------------------------------------------------------------
CREATE TABLE documentation_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_documentation_categories_branch_id ON documentation_categories(branch_id);

-- ------------------------------------------------------------
-- problem_categories
-- ------------------------------------------------------------
CREATE TABLE problem_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_problem_categories_branch_id ON problem_categories(branch_id);

-- ------------------------------------------------------------
-- setting_apps
-- ------------------------------------------------------------
CREATE TABLE setting_apps (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    key         VARCHAR(100) NOT NULL,
    value       TEXT NULL,
    label       VARCHAR(200) NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP NULL
);
CREATE INDEX idx_setting_apps_branch_id ON setting_apps(branch_id);
CREATE UNIQUE INDEX idx_setting_apps_key_branch ON setting_apps(key, branch_id) WHERE deleted_at IS NULL;

-- ============================================================
-- GROUPE 2 — Tables avec dépendances
-- ============================================================

-- ------------------------------------------------------------
-- prestations (→ category_prestations)
-- ------------------------------------------------------------
CREATE TABLE prestations (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id               UUID NOT NULL REFERENCES branches(id),
    category_prestation_id  UUID NULL REFERENCES category_prestations(id),
    name                    VARCHAR(300) NOT NULL,
    price                   DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at              TIMESTAMP NULL,
    created_by              UUID,
    updated_by              UUID
);
CREATE INDEX idx_prestations_branch_id   ON prestations(branch_id);
CREATE INDEX idx_prestations_deleted_at  ON prestations(deleted_at);

-- ------------------------------------------------------------
-- prestation_orders (→ patients, prestations)
-- ------------------------------------------------------------
CREATE TABLE prestation_orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NOT NULL REFERENCES branches(id),
    patient_id      UUID NULL REFERENCES patients(id),
    prestation_id   UUID NULL REFERENCES prestations(id),
    quantity        INTEGER NOT NULL DEFAULT 1,
    total           DECIMAL(10,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP NULL,
    created_by      UUID,
    updated_by      UUID
);
CREATE INDEX idx_prestation_orders_branch_id    ON prestation_orders(branch_id);
CREATE INDEX idx_prestation_orders_patient_id   ON prestation_orders(patient_id);
CREATE INDEX idx_prestation_orders_deleted_at   ON prestation_orders(deleted_at);

-- ------------------------------------------------------------
-- type_consultation_type_consultation_file (pivot)
-- ------------------------------------------------------------
CREATE TABLE type_consultation_type_consultation_file (
    type_consultation_id        UUID NOT NULL REFERENCES type_consultations(id)     ON DELETE CASCADE,
    type_consultation_file_id   UUID NOT NULL REFERENCES type_consultation_files(id) ON DELETE CASCADE,
    PRIMARY KEY (type_consultation_id, type_consultation_file_id)
);

-- ------------------------------------------------------------
-- consultation_type_consultation_files
-- ------------------------------------------------------------
CREATE TABLE consultation_type_consultation_files (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    consultation_id             UUID NOT NULL REFERENCES consultations(id)           ON DELETE CASCADE,
    type_consultation_id        UUID NULL REFERENCES type_consultations(id),
    type_consultation_file_id   UUID NULL REFERENCES type_consultation_files(id),
    file_path                   VARCHAR(500) NULL,
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ctcf_consultation_id ON consultation_type_consultation_files(consultation_id);

-- ------------------------------------------------------------
-- appointments (→ patients, doctors, users)
-- ------------------------------------------------------------
CREATE TABLE appointments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    patient_id  UUID NOT NULL REFERENCES patients(id),
    doctor_id   UUID NULL REFERENCES doctors(id),
    user_id     UUID NULL REFERENCES users(id),
    date        DATE NOT NULL,
    time        TIME NULL,
    notes       TEXT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP NULL,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_appointments_branch_id   ON appointments(branch_id);
CREATE INDEX idx_appointments_patient_id  ON appointments(patient_id);
CREATE INDEX idx_appointments_date        ON appointments(date);
CREATE INDEX idx_appointments_deleted_at  ON appointments(deleted_at);

-- ------------------------------------------------------------
-- assignment_doctors (→ users, reports)
-- ------------------------------------------------------------
CREATE TABLE assignment_doctors (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    report_id   UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_assignment_doctors_branch_id  ON assignment_doctors(branch_id);
CREATE INDEX idx_assignment_doctors_report_id  ON assignment_doctors(report_id);

-- ------------------------------------------------------------
-- appel_by_reports (→ report_titles, branches)
-- NOTE: title_reports renommé en report_titles au début de cette migration
-- ------------------------------------------------------------
CREATE TABLE appel_by_reports (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id         UUID NULL REFERENCES branches(id),
    report_title_id   UUID NULL REFERENCES report_titles(id),
    type              VARCHAR(100) NULL,
    account_id        VARCHAR(255) NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_appel_by_reports_branch_id ON appel_by_reports(branch_id);

-- ------------------------------------------------------------
-- refund_request_logs (→ refund_requests, users)
-- ------------------------------------------------------------
CREATE TABLE refund_request_logs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id           UUID NOT NULL REFERENCES branches(id),
    refund_request_id   UUID NOT NULL REFERENCES refund_requests(id) ON DELETE CASCADE,
    user_id             UUID NULL REFERENCES users(id),
    action              VARCHAR(100) NOT NULL,
    description         TEXT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_refund_request_logs_branch_id          ON refund_request_logs(branch_id);
CREATE INDEX idx_refund_request_logs_refund_request_id  ON refund_request_logs(refund_request_id);

-- ------------------------------------------------------------
-- cashbox_adds (→ cashboxes, banks, invoices, users)
-- ------------------------------------------------------------
CREATE TABLE cashbox_adds (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cashbox_id  UUID NOT NULL REFERENCES cashboxes(id),
    bank_id     UUID NULL REFERENCES banks(id),
    invoice_id  UUID NULL REFERENCES invoices(id),
    user_id     UUID NULL REFERENCES users(id),
    amount      DECIMAL(10,2) NOT NULL DEFAULT 0,
    label       VARCHAR(300) NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_cashbox_adds_cashbox_id ON cashbox_adds(cashbox_id);

-- ------------------------------------------------------------
-- cashbox_ticket_details (→ cashbox_tickets)
-- ------------------------------------------------------------
CREATE TABLE cashbox_ticket_details (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cashbox_ticket_id   UUID NOT NULL REFERENCES cashbox_tickets(id) ON DELETE CASCADE,
    branch_id           UUID NOT NULL REFERENCES branches(id),
    label               VARCHAR(300) NULL,
    amount              DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_cashbox_ticket_details_ticket_id ON cashbox_ticket_details(cashbox_ticket_id);

-- ------------------------------------------------------------
-- expenses (→ users, expense_categories, cashbox_tickets)
-- ------------------------------------------------------------
CREATE TABLE expenses (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id             UUID NOT NULL REFERENCES branches(id),
    user_id               UUID NULL REFERENCES users(id),
    cashbox_ticket_id     UUID NULL REFERENCES cashbox_tickets(id),
    expense_category_id   UUID NULL REFERENCES expense_categories(id),
    label                 VARCHAR(300) NOT NULL,
    amount                DECIMAL(10,2) NOT NULL DEFAULT 0,
    note                  TEXT NULL,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_expenses_branch_id  ON expenses(branch_id);
CREATE INDEX idx_expenses_user_id    ON expenses(user_id);

-- ------------------------------------------------------------
-- expence_details (TYPO PRESERVEE — 'expence' pas 'expense')
-- (→ expenses)
-- ------------------------------------------------------------
CREATE TABLE expence_details (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NULL REFERENCES branches(id),
    expense_id      UUID NULL REFERENCES expenses(id) ON DELETE RESTRICT,
    article_name    VARCHAR(255) NULL,
    article_id      UUID NULL,
    quantity        INTEGER NULL,
    unit_price      INTEGER NULL,
    line_amount     INTEGER NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_expence_details_expense_id ON expence_details(expense_id);

-- ------------------------------------------------------------
-- employee_documents (→ employees)
-- ------------------------------------------------------------
CREATE TABLE employee_documents (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id    UUID NOT NULL REFERENCES branches(id),
    employee_id  UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    name         VARCHAR(300) NOT NULL,
    file_path    VARCHAR(500) NULL,
    type         VARCHAR(100) NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_employee_documents_employee_id ON employee_documents(employee_id);
CREATE INDEX idx_employee_documents_branch_id   ON employee_documents(branch_id);

-- ------------------------------------------------------------
-- users_permissions (RBAC direct → users, permissions)
-- ------------------------------------------------------------
CREATE TABLE users_permissions (
    user_id         UUID NOT NULL REFERENCES users(id)       ON DELETE CASCADE,
    permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, permission_id)
);

-- ------------------------------------------------------------
-- ticket_comments (→ users, tickets)
-- ------------------------------------------------------------
CREATE TABLE ticket_comments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    ticket_id   UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id),
    content     TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_ticket_comments_ticket_id ON ticket_comments(ticket_id);
CREATE INDEX idx_ticket_comments_branch_id ON ticket_comments(branch_id);

-- ------------------------------------------------------------
-- signals (→ users, test_orders)
-- ------------------------------------------------------------
CREATE TABLE signals (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NOT NULL REFERENCES branches(id),
    user_id         UUID NOT NULL REFERENCES users(id),
    test_order_id   UUID NOT NULL REFERENCES test_orders(id),
    type            VARCHAR(100) NULL,
    description     TEXT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_signals_branch_id     ON signals(branch_id);
CREATE INDEX idx_signals_test_order_id ON signals(test_order_id);

-- ------------------------------------------------------------
-- problem_reports (→ test_orders, problem_categories)
-- ------------------------------------------------------------
CREATE TABLE problem_reports (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id             UUID NOT NULL REFERENCES branches(id),
    test_order_id         UUID NOT NULL REFERENCES test_orders(id),
    problem_category_id   UUID NULL REFERENCES problem_categories(id),
    description           TEXT NULL,
    status                VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_problem_reports_branch_id     ON problem_reports(branch_id);
CREATE INDEX idx_problem_reports_test_order_id ON problem_reports(test_order_id);

-- ------------------------------------------------------------
-- docs (→ documentation_categories, users, roles)
-- ------------------------------------------------------------
CREATE TABLE docs (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id                   UUID NOT NULL REFERENCES branches(id),
    documentation_category_id   UUID NULL REFERENCES documentation_categories(id),
    user_id                     UUID NULL REFERENCES users(id),
    role_id                     UUID NULL REFERENCES roles(id),
    title                       VARCHAR(300) NOT NULL,
    content                     TEXT NULL,
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at                  TIMESTAMP NULL,
    created_by                  UUID,
    updated_by                  UUID
);
CREATE INDEX idx_docs_branch_id   ON docs(branch_id);
CREATE INDEX idx_docs_deleted_at  ON docs(deleted_at);

-- ------------------------------------------------------------
-- doc_versions (→ docs, roles, users)
-- ------------------------------------------------------------
CREATE TABLE doc_versions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    doc_id      UUID NOT NULL REFERENCES docs(id) ON DELETE CASCADE,
    role_id     UUID NULL REFERENCES roles(id),
    user_id     UUID NULL REFERENCES users(id),
    content     TEXT NULL,
    version     INTEGER NOT NULL DEFAULT 1,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_doc_versions_doc_id    ON doc_versions(doc_id);
CREATE INDEX idx_doc_versions_branch_id ON doc_versions(branch_id);

-- ------------------------------------------------------------
-- chats (→ users)
-- ------------------------------------------------------------
CREATE TABLE chats (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id    UUID NOT NULL REFERENCES branches(id),
    sender_id    UUID NOT NULL REFERENCES users(id),
    receiver_id  UUID NOT NULL REFERENCES users(id),
    message      TEXT NOT NULL,
    is_read      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_chats_branch_id    ON chats(branch_id);
CREATE INDEX idx_chats_sender_id    ON chats(sender_id);
CREATE INDEX idx_chats_receiver_id  ON chats(receiver_id);
CREATE INDEX idx_chats_is_read      ON chats(is_read);
