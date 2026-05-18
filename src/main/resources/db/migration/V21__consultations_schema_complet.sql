-- V21: enrichissement table consultations (conformité Laravel)

ALTER TABLE consultations
    ADD COLUMN IF NOT EXISTS code                  VARCHAR(50),
    ADD COLUMN IF NOT EXISTS fees                  DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS status                VARCHAR(20)  NOT NULL DEFAULT 'pending',
    ADD COLUMN IF NOT EXISTS motif                 TEXT,
    ADD COLUMN IF NOT EXISTS anamnese              TEXT,
    ADD COLUMN IF NOT EXISTS antecedent            TEXT,
    ADD COLUMN IF NOT EXISTS examen_physique       TEXT,
    ADD COLUMN IF NOT EXISTS diagnostic            TEXT,
    ADD COLUMN IF NOT EXISTS payment_mode          VARCHAR(30)  NOT NULL DEFAULT 'espèce',
    ADD COLUMN IF NOT EXISTS next_appointment      TIMESTAMP,
    ADD COLUMN IF NOT EXISTS prestation_id         UUID REFERENCES prestations(id),
    ADD COLUMN IF NOT EXISTS attribuate_doctor_id  UUID REFERENCES users(id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_consultations_code
    ON consultations(code) WHERE deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS consultation_files (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    consultation_id  UUID NOT NULL REFERENCES consultations(id) ON DELETE CASCADE,
    type_file_label  VARCHAR(200),
    path             TEXT NOT NULL,
    comment          TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_consultation_files_consultation_id
    ON consultation_files(consultation_id);

INSERT INTO permissions (id, name, slug, created_at) VALUES
    (gen_random_uuid(), 'Créer consultation',    'create-consultations', NOW()),
    (gen_random_uuid(), 'Modifier consultation', 'edit-consultations',   NOW()),
    (gen_random_uuid(), 'Supprimer consultation','delete-consultations', NOW())
ON CONFLICT (slug) DO NOTHING;
