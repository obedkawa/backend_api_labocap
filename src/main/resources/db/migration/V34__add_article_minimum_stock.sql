-- V34 — Ajout colonne minimum_stock sur articles

ALTER TABLE articles
    ADD COLUMN IF NOT EXISTS minimum_stock DECIMAL(10,2) NOT NULL DEFAULT 0;
