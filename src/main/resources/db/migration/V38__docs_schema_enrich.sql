-- V38 — Enrichissement des tables docs, doc_versions, documentation_categories

ALTER TABLE docs
    ADD COLUMN IF NOT EXISTS attachment         VARCHAR(500)    NULL,
    ADD COLUMN IF NOT EXISTS file_size          BIGINT          NULL,
    ADD COLUMN IF NOT EXISTS is_current_version BOOLEAN         NOT NULL DEFAULT TRUE;

ALTER TABLE doc_versions
    ADD COLUMN IF NOT EXISTS attachment  VARCHAR(500)    NULL,
    ADD COLUMN IF NOT EXISTS file_size   BIGINT          NULL,
    ADD COLUMN IF NOT EXISTS title       VARCHAR(300)    NULL,
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP       NULL,
    ADD COLUMN IF NOT EXISTS created_by  UUID            NULL,
    ADD COLUMN IF NOT EXISTS updated_by  UUID            NULL;

CREATE INDEX IF NOT EXISTS idx_doc_versions_deleted_at ON doc_versions(deleted_at);

ALTER TABLE documentation_categories
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP       NULL,
    ADD COLUMN IF NOT EXISTS created_by  UUID            NULL,
    ADD COLUMN IF NOT EXISTS updated_by  UUID            NULL;

CREATE INDEX IF NOT EXISTS idx_documentation_categories_deleted_at ON documentation_categories(deleted_at);
