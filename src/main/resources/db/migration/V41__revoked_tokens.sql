CREATE TABLE revoked_tokens (
    jti         VARCHAR(36)  NOT NULL PRIMARY KEY,
    revoked_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP    NOT NULL
);

CREATE INDEX idx_revoked_tokens_expires_at ON revoked_tokens(expires_at);
