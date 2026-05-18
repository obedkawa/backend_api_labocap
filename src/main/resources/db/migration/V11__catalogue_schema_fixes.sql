-- V11: corrections schéma catalogue (conformité Laravel)

-- 1. Renommer la colonne name→title dans type_orders (conformité Laravel)
ALTER TABLE type_orders RENAME COLUMN name TO title;

-- 2. Soft-delete pour type_orders (deleted_at absent de V1)
ALTER TABLE type_orders ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
CREATE INDEX IF NOT EXISTS idx_type_orders_deleted_at ON type_orders(deleted_at);

-- 3. Soft-delete pour unit_measurements (deleted_at absent de V1)
ALTER TABLE unit_measurements ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
CREATE INDEX IF NOT EXISTS idx_unit_measurements_deleted_at ON unit_measurements(deleted_at);

-- 4. Soft-delete pour data_codes (deleted_at absent)
ALTER TABLE data_codes ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITH TIME ZONE;
CREATE INDEX IF NOT EXISTS idx_data_codes_deleted_at ON data_codes(deleted_at);
