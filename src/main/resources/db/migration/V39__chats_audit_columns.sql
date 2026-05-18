ALTER TABLE chats
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS created_by  UUID      NULL,
    ADD COLUMN IF NOT EXISTS updated_by  UUID      NULL;

CREATE INDEX IF NOT EXISTS idx_chats_deleted_at ON chats(deleted_at);
