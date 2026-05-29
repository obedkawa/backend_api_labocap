-- V41: Ajout des colonnes pour la réinitialisation de mot de passe
-- reset_token : UUID généré côté applicatif, transmis à l'utilisateur (dev: retourné dans la réponse JSON)
-- reset_token_expires_at : expiration du token (1 heure après génération)

ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token_expires_at TIMESTAMP;
