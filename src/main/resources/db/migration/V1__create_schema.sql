-- ============================================================
-- V1 - Création du schéma complet LIS Labo-Anapath
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ------------------------------------------------------------
-- branches
-- ------------------------------------------------------------
CREATE TABLE branches (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(200)  NOT NULL,
    address     TEXT,
    phone       VARCHAR(20),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP
);
CREATE INDEX idx_branches_deleted_at ON branches(deleted_at);

-- ------------------------------------------------------------
-- users
-- ------------------------------------------------------------
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    name        VARCHAR(200)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    phone       VARCHAR(20),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_users_email        ON users(email);
CREATE INDEX idx_users_branch_id    ON users(branch_id);
CREATE INDEX idx_users_deleted_at   ON users(deleted_at);

-- ------------------------------------------------------------
-- roles
-- ------------------------------------------------------------
CREATE TABLE roles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_roles_branch_id ON roles(branch_id);
CREATE INDEX idx_roles_slug      ON roles(slug);

-- ------------------------------------------------------------
-- permissions
-- ------------------------------------------------------------
CREATE TABLE permissions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150) NOT NULL,
    slug        VARCHAR(150) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_permissions_slug ON permissions(slug);

-- ------------------------------------------------------------
-- user_roles
-- ------------------------------------------------------------
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ------------------------------------------------------------
-- role_permissions
-- ------------------------------------------------------------
CREATE TABLE role_permissions (
    role_id       UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- ------------------------------------------------------------
-- two_fas — structure Laravel: code temporaire OTP par utilisateur
-- V6 recrée cette table avec le bon schéma (DROP + CREATE)
-- ------------------------------------------------------------
CREATE TABLE two_fas (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(255) NOT NULL,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- patients
-- ------------------------------------------------------------
CREATE TABLE patients (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    birth_date  DATE,
    gender      VARCHAR(10),
    address     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_patients_branch_id  ON patients(branch_id);
CREATE INDEX idx_patients_phone      ON patients(phone);
CREATE INDEX idx_patients_deleted_at ON patients(deleted_at);
CREATE UNIQUE INDEX idx_patients_phone_branch ON patients(phone, branch_id) WHERE phone IS NOT NULL AND deleted_at IS NULL;

-- ------------------------------------------------------------
-- hospitals
-- ------------------------------------------------------------
CREATE TABLE hospitals (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id           UUID NOT NULL,
    name                VARCHAR(200) NOT NULL,
    phone               VARCHAR(20),
    address             TEXT,
    contract_permission TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP,
    created_by          UUID,
    updated_by          UUID
);
CREATE INDEX idx_hospitals_branch_id  ON hospitals(branch_id);
CREATE INDEX idx_hospitals_deleted_at ON hospitals(deleted_at);

-- ------------------------------------------------------------
-- doctors
-- ------------------------------------------------------------
CREATE TABLE doctors (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    speciality  VARCHAR(200),
    hospital_id UUID REFERENCES hospitals(id),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_doctors_branch_id  ON doctors(branch_id);
CREATE INDEX idx_doctors_deleted_at ON doctors(deleted_at);

-- ------------------------------------------------------------
-- category_tests
-- ------------------------------------------------------------
CREATE TABLE category_tests (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_category_tests_branch_id ON category_tests(branch_id);

-- ------------------------------------------------------------
-- unit_measurements
-- ------------------------------------------------------------
CREATE TABLE unit_measurements (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id      UUID NOT NULL,
    name           VARCHAR(100) NOT NULL,
    abbreviation   VARCHAR(20),
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_unit_measurements_branch_id ON unit_measurements(branch_id);

-- ------------------------------------------------------------
-- type_orders
-- ------------------------------------------------------------
CREATE TABLE type_orders (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_type_orders_branch_id ON type_orders(branch_id);

-- ------------------------------------------------------------
-- lab_tests
-- ------------------------------------------------------------
CREATE TABLE lab_tests (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id           UUID NOT NULL,
    name                VARCHAR(300) NOT NULL,
    code                VARCHAR(50),
    price               DECIMAL(10,2) NOT NULL DEFAULT 0,
    normal_value        TEXT,
    category_test_id    UUID REFERENCES category_tests(id),
    unit_measurement_id UUID REFERENCES unit_measurements(id),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP,
    created_by          UUID,
    updated_by          UUID
);
CREATE INDEX idx_lab_tests_branch_id  ON lab_tests(branch_id);
CREATE INDEX idx_lab_tests_deleted_at ON lab_tests(deleted_at);

-- ------------------------------------------------------------
-- contrats
-- ------------------------------------------------------------
CREATE TABLE contrats (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    hospital_id UUID REFERENCES hospitals(id),
    client_id   UUID,
    nbr_tests   INT NOT NULL DEFAULT 0,
    start_date  DATE NOT NULL,
    end_date    DATE,
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_contrats_branch_id  ON contrats(branch_id);
CREATE INDEX idx_contrats_deleted_at ON contrats(deleted_at);

-- ------------------------------------------------------------
-- details_contrats (renommé details__contrats en V3 — typo Laravel préservée)
-- Laravel: contrat_id, pourcentage integer nullable, category_test_id FK
-- ------------------------------------------------------------
CREATE TABLE details_contrats (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contrat_id        UUID NOT NULL REFERENCES contrats(id) ON DELETE CASCADE,
    category_test_id  UUID NOT NULL REFERENCES category_tests(id),
    pourcentage       INTEGER,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_details_contrats_contrat_id ON details_contrats(contrat_id);

-- ------------------------------------------------------------
-- test_orders
-- ------------------------------------------------------------
CREATE TABLE test_orders (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id        UUID NOT NULL,
    code             VARCHAR(50) NOT NULL UNIQUE,
    patient_id       UUID NOT NULL REFERENCES patients(id),
    doctor_id        UUID REFERENCES doctors(id),
    hospital_id      UUID REFERENCES hospitals(id),
    contrat_id       UUID REFERENCES contrats(id),
    type_order_id    UUID REFERENCES type_orders(id),
    status           VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    prelevement_date DATE NOT NULL,
    examen_files     TEXT,
    is_called        BOOLEAN NOT NULL DEFAULT FALSE,
    called_at        TIMESTAMP,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at       TIMESTAMP,
    created_by       UUID,
    updated_by       UUID
);
CREATE INDEX idx_test_orders_branch_id    ON test_orders(branch_id);
CREATE INDEX idx_test_orders_code         ON test_orders(code);
CREATE INDEX idx_test_orders_patient_id   ON test_orders(patient_id);
CREATE INDEX idx_test_orders_status       ON test_orders(status);
CREATE INDEX idx_test_orders_deleted_at   ON test_orders(deleted_at);

-- ------------------------------------------------------------
-- detail_test_orders
-- ------------------------------------------------------------
CREATE TABLE detail_test_orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_order_id   UUID NOT NULL REFERENCES test_orders(id) ON DELETE CASCADE,
    lab_test_id     UUID NOT NULL REFERENCES lab_tests(id),
    result          TEXT,
    normal_value    TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_detail_test_orders_test_order_id ON detail_test_orders(test_order_id);

-- ------------------------------------------------------------
-- test_order_assignments
-- ------------------------------------------------------------
CREATE TABLE test_order_assignments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NOT NULL,
    test_order_id   UUID NOT NULL REFERENCES test_orders(id),
    user_id         UUID NOT NULL REFERENCES users(id),
    code            VARCHAR(50),
    note            TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_test_order_assignments_branch_id     ON test_order_assignments(branch_id);
CREATE INDEX idx_test_order_assignments_test_order_id ON test_order_assignments(test_order_id);

-- ------------------------------------------------------------
-- test_order_assignment_details
-- ------------------------------------------------------------
CREATE TABLE test_order_assignment_details (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id   UUID NOT NULL REFERENCES test_order_assignments(id) ON DELETE CASCADE,
    lab_test_id     UUID NOT NULL REFERENCES lab_tests(id),
    code            VARCHAR(50),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_toad_assignment_id ON test_order_assignment_details(assignment_id);

-- ------------------------------------------------------------
-- title_reports
-- ------------------------------------------------------------
CREATE TABLE title_reports (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(300) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_title_reports_branch_id ON title_reports(branch_id);

-- ------------------------------------------------------------
-- tags
-- ------------------------------------------------------------
CREATE TABLE tags (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_tags_branch_id ON tags(branch_id);

-- ------------------------------------------------------------
-- reports
-- ------------------------------------------------------------
CREATE TABLE reports (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id            UUID NOT NULL,
    test_order_id        UUID NOT NULL REFERENCES test_orders(id),
    title_id             UUID REFERENCES title_reports(id),
    content              TEXT,
    comment              TEXT,
    status               VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    reviewed_by_user_id  UUID REFERENCES users(id),
    signatory1           UUID REFERENCES users(id),
    signatory2           UUID REFERENCES users(id),
    signatory3           UUID REFERENCES users(id),
    is_delivered         BOOLEAN NOT NULL DEFAULT FALSE,
    receiver_name        VARCHAR(200),
    receiver_signature   TEXT,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at           TIMESTAMP,
    created_by           UUID,
    updated_by           UUID
);
CREATE INDEX idx_reports_branch_id    ON reports(branch_id);
CREATE INDEX idx_reports_test_order   ON reports(test_order_id);
CREATE INDEX idx_reports_deleted_at   ON reports(deleted_at);

-- ------------------------------------------------------------
-- report_tags
-- ------------------------------------------------------------
CREATE TABLE report_tags (
    report_id UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    tag_id    UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (report_id, tag_id)
);

-- ------------------------------------------------------------
-- test_pathology_macros
-- ------------------------------------------------------------
CREATE TABLE test_pathology_macros (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    title       VARCHAR(300) NOT NULL,
    content     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP
);
CREATE INDEX idx_test_pathology_macros_branch_id ON test_pathology_macros(branch_id);

-- ------------------------------------------------------------
-- log_reports
-- ------------------------------------------------------------
CREATE TABLE log_reports (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    report_id   UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id),
    action      VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_log_reports_branch_id ON log_reports(branch_id);
CREATE INDEX idx_log_reports_report_id ON log_reports(report_id);

-- ------------------------------------------------------------
-- setting_report_templates
-- ------------------------------------------------------------
CREATE TABLE setting_report_templates (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(200) NOT NULL,
    header      TEXT,
    footer      TEXT,
    logo_path   VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_setting_report_templates_branch_id ON setting_report_templates(branch_id);

-- ------------------------------------------------------------
-- type_consultations
-- ------------------------------------------------------------
CREATE TABLE type_consultations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_type_consultations_branch_id ON type_consultations(branch_id);

-- ------------------------------------------------------------
-- consultations
-- ------------------------------------------------------------
CREATE TABLE consultations (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id             UUID NOT NULL,
    patient_id            UUID NOT NULL REFERENCES patients(id),
    doctor_id             UUID REFERENCES doctors(id),
    type_consultation_id  UUID REFERENCES type_consultations(id),
    notes                 TEXT,
    date                  DATE NOT NULL,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at            TIMESTAMP,
    created_by            UUID,
    updated_by            UUID
);
CREATE INDEX idx_consultations_branch_id    ON consultations(branch_id);
CREATE INDEX idx_consultations_patient_id   ON consultations(patient_id);
CREATE INDEX idx_consultations_deleted_at   ON consultations(deleted_at);

-- ------------------------------------------------------------
-- invoices
-- ------------------------------------------------------------
CREATE TABLE invoices (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NOT NULL,
    test_order_id   UUID NOT NULL REFERENCES test_orders(id),
    patient_id      UUID NOT NULL REFERENCES patients(id),
    total           DECIMAL(10,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date        DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    created_by      UUID,
    updated_by      UUID
);
CREATE INDEX idx_invoices_branch_id    ON invoices(branch_id);
CREATE INDEX idx_invoices_patient_id   ON invoices(patient_id);
CREATE INDEX idx_invoices_deleted_at   ON invoices(deleted_at);

-- ------------------------------------------------------------
-- invoice_details
-- ------------------------------------------------------------
CREATE TABLE invoice_details (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id  UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    lab_test_id UUID NOT NULL REFERENCES lab_tests(id),
    quantity    INT NOT NULL DEFAULT 1,
    unit_price  DECIMAL(10,2) NOT NULL DEFAULT 0,
    total       DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_invoice_details_invoice_id ON invoice_details(invoice_id);

-- ------------------------------------------------------------
-- payments
-- ------------------------------------------------------------
CREATE TABLE payments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id    UUID NOT NULL,
    invoice_id   UUID NOT NULL REFERENCES invoices(id),
    amount       DECIMAL(10,2) NOT NULL DEFAULT 0,
    method       VARCHAR(30) NOT NULL DEFAULT 'CASH',
    payment_date DATE NOT NULL,
    notes        TEXT,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by   UUID,
    updated_by   UUID
);
CREATE INDEX idx_payments_branch_id   ON payments(branch_id);
CREATE INDEX idx_payments_invoice_id  ON payments(invoice_id);

-- ------------------------------------------------------------
-- cashboxes
-- ------------------------------------------------------------
CREATE TABLE cashboxes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(100) NOT NULL,
    balance     DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_cashboxes_branch_id ON cashboxes(branch_id);

-- ------------------------------------------------------------
-- cashbox_tickets
-- ------------------------------------------------------------
CREATE TABLE cashbox_tickets (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    cashbox_id  UUID NOT NULL REFERENCES cashboxes(id),
    label       VARCHAR(300) NOT NULL,
    amount      DECIMAL(10,2) NOT NULL DEFAULT 0,
    type        VARCHAR(10) NOT NULL,
    payment_id  UUID REFERENCES payments(id),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_cashbox_tickets_branch_id   ON cashbox_tickets(branch_id);
CREATE INDEX idx_cashbox_tickets_cashbox_id  ON cashbox_tickets(cashbox_id);

-- ------------------------------------------------------------
-- cashbox_dailies
-- ------------------------------------------------------------
CREATE TABLE cashbox_dailies (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id        UUID NOT NULL,
    cashbox_id       UUID NOT NULL REFERENCES cashboxes(id),
    opening_balance  DECIMAL(12,2) NOT NULL DEFAULT 0,
    closing_balance  DECIMAL(12,2) NOT NULL DEFAULT 0,
    date             DATE NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_cashbox_dailies_branch_id   ON cashbox_dailies(branch_id);
CREATE INDEX idx_cashbox_dailies_cashbox_id  ON cashbox_dailies(cashbox_id);

-- ------------------------------------------------------------
-- refund_requests
-- ------------------------------------------------------------
CREATE TABLE refund_requests (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    payment_id  UUID NOT NULL REFERENCES payments(id),
    reason      TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount      DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_refund_requests_branch_id  ON refund_requests(branch_id);
CREATE INDEX idx_refund_requests_payment_id ON refund_requests(payment_id);

-- ------------------------------------------------------------
-- employees
-- ------------------------------------------------------------
CREATE TABLE employees (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    user_id     UUID REFERENCES users(id),
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    position    VARCHAR(200),
    salary      DECIMAL(10,2) NOT NULL DEFAULT 0,
    hire_date   DATE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_employees_branch_id   ON employees(branch_id);
CREATE INDEX idx_employees_deleted_at  ON employees(deleted_at);

-- ------------------------------------------------------------
-- employee_contrats
-- ------------------------------------------------------------
CREATE TABLE employee_contrats (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id  UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    start_date   DATE NOT NULL,
    end_date     DATE,
    type         VARCHAR(50),
    salary       DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_employee_contrats_employee_id ON employee_contrats(employee_id);

-- ------------------------------------------------------------
-- employee_payrolls
-- ------------------------------------------------------------
CREATE TABLE employee_payrolls (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id   UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    month         INT NOT NULL,
    year          INT NOT NULL,
    gross_salary  DECIMAL(10,2) NOT NULL DEFAULT 0,
    deductions    DECIMAL(10,2) NOT NULL DEFAULT 0,
    net_salary    DECIMAL(10,2) NOT NULL DEFAULT 0,
    paid_at       DATE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_employee_payrolls_employee_id ON employee_payrolls(employee_id);

-- ------------------------------------------------------------
-- employee_timeoffs
-- ------------------------------------------------------------
CREATE TABLE employee_timeoffs (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id  UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    start_date   DATE NOT NULL,
    end_date     DATE NOT NULL,
    reason       TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_employee_timeoffs_employee_id ON employee_timeoffs(employee_id);

-- ------------------------------------------------------------
-- suppliers
-- ------------------------------------------------------------
CREATE TABLE suppliers (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    name        VARCHAR(200) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    address     TEXT,
    category    VARCHAR(100),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP
);
CREATE INDEX idx_suppliers_branch_id   ON suppliers(branch_id);
CREATE INDEX idx_suppliers_deleted_at  ON suppliers(deleted_at);

-- ------------------------------------------------------------
-- articles
-- ------------------------------------------------------------
CREATE TABLE articles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id       UUID NOT NULL,
    name            VARCHAR(300) NOT NULL,
    code            VARCHAR(50),
    quantity        DECIMAL(10,2) NOT NULL DEFAULT 0,
    unit            VARCHAR(50),
    purchase_price  DECIMAL(10,2) NOT NULL DEFAULT 0,
    supplier_id     UUID REFERENCES suppliers(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP,
    created_by      UUID,
    updated_by      UUID
);
CREATE INDEX idx_articles_branch_id   ON articles(branch_id);
CREATE INDEX idx_articles_deleted_at  ON articles(deleted_at);

-- ------------------------------------------------------------
-- movements
-- ------------------------------------------------------------
CREATE TABLE movements (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL,
    article_id  UUID NOT NULL REFERENCES articles(id),
    type        VARCHAR(10) NOT NULL,
    quantity    DECIMAL(10,2) NOT NULL DEFAULT 0,
    notes       TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID
);
CREATE INDEX idx_movements_branch_id   ON movements(branch_id);
CREATE INDEX idx_movements_article_id  ON movements(article_id);

-- ------------------------------------------------------------
-- settings
-- ------------------------------------------------------------
CREATE TABLE settings (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id    UUID NOT NULL,
    key          VARCHAR(100) NOT NULL,
    value        TEXT,
    placeholder  VARCHAR(200),
    ico          VARCHAR(100),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at   TIMESTAMP
);
CREATE INDEX idx_settings_branch_id   ON settings(branch_id);
CREATE INDEX idx_settings_deleted_at  ON settings(deleted_at);
CREATE UNIQUE INDEX idx_settings_key_branch ON settings(key, branch_id) WHERE deleted_at IS NULL;

-- ------------------------------------------------------------
-- tickets
-- ------------------------------------------------------------
CREATE TABLE tickets (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id    UUID NOT NULL,
    user_id      UUID NOT NULL REFERENCES users(id),
    title        VARCHAR(300) NOT NULL,
    description  TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    priority     VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at   TIMESTAMP,
    created_by   UUID,
    updated_by   UUID
);
CREATE INDEX idx_tickets_branch_id   ON tickets(branch_id);
CREATE INDEX idx_tickets_deleted_at  ON tickets(deleted_at);
CREATE INDEX idx_tickets_status      ON tickets(status);
