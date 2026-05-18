-- S'assurer que files_name existe sur test_orders
ALTER TABLE test_orders
    ADD COLUMN IF NOT EXISTS files_name TEXT;

-- Index de recherche sur les macros pathologiques
CREATE INDEX IF NOT EXISTS idx_macros_search
    ON test_pathology_macros USING gin(
        to_tsvector('french', coalesce(title, '') || ' ' || coalesce(content, ''))
    );
