-- V50: Aligner setting_report_templates avec le schéma Laravel
-- Source de vérité : labo-anapath-main (Laravel)
-- Colonnes Laravel : title, description, detail, content
-- Colonnes refonte conservées : name, header, footer, logo_path

ALTER TABLE setting_report_templates ADD COLUMN IF NOT EXISTS title VARCHAR(255);
ALTER TABLE setting_report_templates ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE setting_report_templates ADD COLUMN IF NOT EXISTS detail TEXT;
ALTER TABLE setting_report_templates ADD COLUMN IF NOT EXISTS content TEXT;

-- 'name' devient nullable car absent côté Laravel
ALTER TABLE setting_report_templates ALTER COLUMN name DROP NOT NULL;
