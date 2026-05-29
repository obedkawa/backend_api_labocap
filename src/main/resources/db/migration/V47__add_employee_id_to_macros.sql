-- V47: Ajout de l'identifiant du laborantin à test_pathology_macros
-- macro_date existe déjà depuis V16, seul employee_id est ajouté.
ALTER TABLE test_pathology_macros ADD COLUMN IF NOT EXISTS employee_id UUID;
