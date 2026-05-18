-- Add 2FA and user tracking columns to users table
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS two_factor_enabled  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS two_factor_secret   VARCHAR(255),
    ADD COLUMN IF NOT EXISTS is_connect          BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS opt                 INTEGER,
    ADD COLUMN IF NOT EXISTS lastlogindevice     VARCHAR(255),
    ADD COLUMN IF NOT EXISTS signature           TEXT,
    ADD COLUMN IF NOT EXISTS email_notification  BOOLEAN NOT NULL DEFAULT FALSE;

-- two_fas corrigé dans V6 (V1 créait une structure incorrecte)
