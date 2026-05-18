-- Champs manquants sur reports
ALTER TABLE reports
    ADD COLUMN IF NOT EXISTS signature_date                    TIMESTAMP,
    ADD COLUMN IF NOT EXISTS delivery_date                     TIMESTAMP,
    ADD COLUMN IF NOT EXISTS is_called                         BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS call_date                         TIMESTAMP,
    ADD COLUMN IF NOT EXISTS content_micro                     TEXT,
    ADD COLUMN IF NOT EXISTS comment_sup                       TEXT,
    ADD COLUMN IF NOT EXISTS description_supplementaire        TEXT,
    ADD COLUMN IF NOT EXISTS description_supplementaire_micro  TEXT,
    ADD COLUMN IF NOT EXISTS retriever_name                    VARCHAR(200),
    ADD COLUMN IF NOT EXISTS retriever_signature               TEXT;

-- Table de jointure report_tags (si absente)
CREATE TABLE IF NOT EXISTS report_tags (
    report_id UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    tag_id    UUID NOT NULL REFERENCES tags(id)    ON DELETE CASCADE,
    PRIMARY KEY (report_id, tag_id)
);

-- Index pour filtrage par médecin signataire et date de signature
CREATE INDEX IF NOT EXISTS idx_reports_signatory1      ON reports(signatory1);
CREATE INDEX IF NOT EXISTS idx_reports_signature_date  ON reports(signature_date);
CREATE INDEX IF NOT EXISTS idx_reports_branch_created  ON reports(branch_id, created_at DESC);
