-- V49: Aligner title_reports avec Laravel (colonne status / is_default)
-- Permet de marquer un titre de rapport comme "par défaut"
ALTER TABLE title_reports ADD COLUMN IF NOT EXISTS is_default BOOLEAN NOT NULL DEFAULT FALSE;
