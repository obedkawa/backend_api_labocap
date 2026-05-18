-- ============================================================
-- V2 - Seed permissions and ADMIN role
-- ============================================================

INSERT INTO permissions (id, name, slug, created_at) VALUES
    (gen_random_uuid(), 'Voir patients',              'view-patients',           NOW()),
    (gen_random_uuid(), 'Créer patients',             'create-patients',         NOW()),
    (gen_random_uuid(), 'Modifier patients',          'edit-patients',           NOW()),
    (gen_random_uuid(), 'Supprimer patients',         'delete-patients',         NOW()),

    (gen_random_uuid(), 'Voir bons d''examen',        'view-test-orders',        NOW()),
    (gen_random_uuid(), 'Créer bons d''examen',       'create-test-orders',      NOW()),
    (gen_random_uuid(), 'Modifier bons d''examen',    'edit-test-orders',        NOW()),
    (gen_random_uuid(), 'Supprimer bons d''examen',   'delete-test-orders',      NOW()),

    (gen_random_uuid(), 'Voir comptes-rendus',        'view-reports',            NOW()),
    (gen_random_uuid(), 'Créer comptes-rendus',       'create-reports',          NOW()),
    (gen_random_uuid(), 'Modifier comptes-rendus',    'edit-reports',            NOW()),
    (gen_random_uuid(), 'Valider comptes-rendus',     'validate-reports',        NOW()),
    (gen_random_uuid(), 'Livrer comptes-rendus',      'deliver-reports',         NOW()),

    (gen_random_uuid(), 'Voir médecins',              'view-doctors',            NOW()),
    (gen_random_uuid(), 'Gérer médecins',             'manage-doctors',          NOW()),

    (gen_random_uuid(), 'Voir hôpitaux',              'view-hospitals',          NOW()),
    (gen_random_uuid(), 'Gérer hôpitaux',             'manage-hospitals',        NOW()),

    (gen_random_uuid(), 'Voir analyses',              'view-tests',              NOW()),
    (gen_random_uuid(), 'Gérer analyses',             'manage-tests',            NOW()),

    (gen_random_uuid(), 'Voir finances',              'view-finance',            NOW()),
    (gen_random_uuid(), 'Gérer factures',             'manage-invoices',         NOW()),
    (gen_random_uuid(), 'Gérer paiements',            'manage-payments',         NOW()),
    (gen_random_uuid(), 'Gérer caisse',               'manage-cashbox',          NOW()),

    (gen_random_uuid(), 'Voir RH',                   'view-hr',                 NOW()),
    (gen_random_uuid(), 'Gérer employés',             'manage-employees',        NOW()),

    (gen_random_uuid(), 'Voir inventaire',            'view-inventory',          NOW()),
    (gen_random_uuid(), 'Gérer inventaire',           'manage-inventory',        NOW()),

    (gen_random_uuid(), 'Voir consultations',         'view-consultations',      NOW()),
    (gen_random_uuid(), 'Gérer consultations',        'manage-consultations',    NOW()),

    (gen_random_uuid(), 'Voir contrats',              'view-contracts',          NOW()),
    (gen_random_uuid(), 'Gérer contrats',             'manage-contracts',        NOW()),

    (gen_random_uuid(), 'Gérer utilisateurs',         'manage-users',            NOW()),
    (gen_random_uuid(), 'Gérer rôles',                'manage-roles',            NOW()),
    (gen_random_uuid(), 'Gérer agences',              'manage-branches',         NOW()),

    (gen_random_uuid(), 'Voir paramètres',            'view-settings',           NOW()),
    (gen_random_uuid(), 'Gérer paramètres',           'manage-settings',         NOW()),

    (gen_random_uuid(), 'Voir support',               'view-support',            NOW()),
    (gen_random_uuid(), 'Gérer support',              'manage-support',          NOW());

-- Create a placeholder branch for the ADMIN role seed
-- (in production the branch would already exist)
DO $$
DECLARE
    v_branch_id UUID := '00000000-0000-0000-0000-000000000001';
    v_role_id   UUID := gen_random_uuid();
BEGIN
    -- Insert branch if not exists
    INSERT INTO branches (id, name, created_at, updated_at)
    VALUES (v_branch_id, 'Siège', NOW(), NOW())
    ON CONFLICT (id) DO NOTHING;

    -- Insert ADMIN role
    INSERT INTO roles (id, branch_id, name, slug, created_at, updated_at)
    VALUES (v_role_id, v_branch_id, 'Administrateur', 'admin', NOW(), NOW())
    ON CONFLICT (slug) DO NOTHING;

    -- Assign all permissions to ADMIN role
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT v_role_id, id FROM permissions
    ON CONFLICT DO NOTHING;
END $$;
