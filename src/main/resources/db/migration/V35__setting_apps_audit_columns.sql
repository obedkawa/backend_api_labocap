-- V35 — Ajout colonnes d'audit sur setting_apps

ALTER TABLE setting_apps
    ADD COLUMN IF NOT EXISTS created_by UUID,
    ADD COLUMN IF NOT EXISTS updated_by UUID;
