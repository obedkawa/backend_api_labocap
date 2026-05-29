-- Epic 7 — Enrichissement des tables inventaire (champs manquants vs Laravel)

-- Articles : description, numéro de lot, date d'expiration
ALTER TABLE articles ADD COLUMN IF NOT EXISTS description   TEXT;
ALTER TABLE articles ADD COLUMN IF NOT EXISTS lot_number    VARCHAR(100);
ALTER TABLE articles ADD COLUMN IF NOT EXISTS expiration_date DATE;

-- Mouvements : utilisateur qui a effectué le mouvement + date explicite
ALTER TABLE movements ADD COLUMN IF NOT EXISTS user_id        UUID;
ALTER TABLE movements ADD COLUMN IF NOT EXISTS movement_date  DATE;

ALTER TABLE movements
    ADD CONSTRAINT fk_movements_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Fournisseurs : informations complémentaires
ALTER TABLE suppliers ADD COLUMN IF NOT EXISTS information TEXT;
