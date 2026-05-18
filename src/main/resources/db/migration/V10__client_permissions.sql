-- Ajout des permissions granulaires clients (conformité Laravel)
INSERT INTO permissions (id, name, slug, branch_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Créer des clients', 'create-clients',
       b.id, NOW(), NOW()
FROM branches b
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.slug = 'create-clients' AND p.branch_id = b.id
);

INSERT INTO permissions (id, name, slug, branch_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Modifier des clients', 'edit-clients',
       b.id, NOW(), NOW()
FROM branches b
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.slug = 'edit-clients' AND p.branch_id = b.id
);

INSERT INTO permissions (id, name, slug, branch_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Supprimer des clients', 'delete-clients',
       b.id, NOW(), NOW()
FROM branches b
WHERE NOT EXISTS (
    SELECT 1 FROM permissions p WHERE p.slug = 'delete-clients' AND p.branch_id = b.id
);

-- Assigner les nouvelles permissions au rôle admin de chaque branche
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.branch_id = r.branch_id
    AND p.slug IN ('create-clients', 'edit-clients', 'delete-clients')
WHERE r.slug = 'admin'
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);
