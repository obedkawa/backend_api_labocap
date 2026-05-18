-- V22: gestion des rendez-vous

-- 1. Table appointments
CREATE TABLE IF NOT EXISTS appointments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    patient_id  UUID NOT NULL REFERENCES patients(id),
    user_id     UUID REFERENCES users(id),
    date        TIMESTAMP,
    priority    VARCHAR(20) NOT NULL DEFAULT 'normal',
    status      VARCHAR(20) NOT NULL DEFAULT 'pending',
    message     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);

CREATE INDEX IF NOT EXISTS idx_appointments_branch_id  ON appointments(branch_id);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_date       ON appointments(date);
CREATE INDEX IF NOT EXISTS idx_appointments_deleted_at ON appointments(deleted_at);

-- 2. Lien appointment_id sur consultations
ALTER TABLE consultations
    ADD COLUMN IF NOT EXISTS appointment_id UUID REFERENCES appointments(id);

CREATE INDEX IF NOT EXISTS idx_consultations_appointment_id ON consultations(appointment_id);

-- 3. Permissions
INSERT INTO permissions (id, name, slug, created_at) VALUES
    (gen_random_uuid(), 'Voir rendez-vous',      'view-appointments',   NOW()),
    (gen_random_uuid(), 'Créer rendez-vous',     'create-appointments', NOW()),
    (gen_random_uuid(), 'Modifier rendez-vous',  'edit-appointments',   NOW()),
    (gen_random_uuid(), 'Supprimer rendez-vous', 'delete-appointments', NOW())
ON CONFLICT (slug) DO NOTHING;
