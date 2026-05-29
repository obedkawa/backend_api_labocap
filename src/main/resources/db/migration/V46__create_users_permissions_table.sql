-- V46: Table de jointure permissions directes par utilisateur
-- Permet d'assigner des permissions individuelles sans passer par un rôle
CREATE TABLE IF NOT EXISTS users_permissions (
    user_id       UUID NOT NULL,
    permission_id UUID NOT NULL,
    branch_id     UUID,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_users_permissions PRIMARY KEY (user_id, permission_id),
    CONSTRAINT fk_up_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_up_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_users_permissions_user_id ON users_permissions(user_id);
