-- ============================================================
-- V5 - Add clients table and permissions
-- ============================================================

CREATE TABLE clients (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id   UUID NOT NULL REFERENCES branches(id),
    ifu         VARCHAR(255) UNIQUE,
    name        VARCHAR(255) NOT NULL,
    adress      VARCHAR(255),
    contact     VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);

INSERT INTO permissions (id, name, slug, created_at) VALUES
    (gen_random_uuid(), 'Voir clients',  'view-clients',   NOW()),
    (gen_random_uuid(), 'Gérer clients', 'manage-clients', NOW());

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.slug = 'admin'
  AND p.slug IN ('view-clients', 'manage-clients')
ON CONFLICT DO NOTHING;
