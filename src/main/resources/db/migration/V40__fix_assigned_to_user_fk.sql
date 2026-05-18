-- V40: Correction de la contrainte FK sur test_orders.assigned_to_user_id
-- La V12 avait créé cette FK avec ON DELETE RESTRICT (incorrect).
-- Elle doit être ON DELETE SET NULL pour permettre la suppression d'un utilisateur
-- sans bloquer les bons d'examen qui lui étaient assignés.

ALTER TABLE test_orders DROP CONSTRAINT IF EXISTS test_orders_assigned_to_user_id_fkey;
ALTER TABLE test_orders ADD CONSTRAINT test_orders_assigned_to_user_id_fkey
    FOREIGN KEY (assigned_to_user_id) REFERENCES users(id) ON DELETE SET NULL;
