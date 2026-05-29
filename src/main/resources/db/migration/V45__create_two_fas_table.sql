-- V45: Table dédiée au stockage des codes 2FA par email
-- code : bcrypt hash du code OTP à 6 chiffres (jamais stocké en clair)
-- user_id : un seul code actif par utilisateur (unique)
-- created_at : sert à calculer l'expiration (10 minutes)
CREATE TABLE IF NOT EXISTS two_fas (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    code        VARCHAR(255) NOT NULL,
    user_id     UUID        NOT NULL,
    branch_id   UUID,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_two_fas PRIMARY KEY (id),
    CONSTRAINT uq_two_fas_user_id UNIQUE (user_id),
    CONSTRAINT fk_two_fas_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_two_fas_user_id ON two_fas(user_id);
