-- V26 — Champs Mobile Money Sckaler sur payments (story 5-4)

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS payment_name   VARCHAR(50),
    ADD COLUMN IF NOT EXISTS payment_number VARCHAR(20),
    ADD COLUMN IF NOT EXISTS payment_status VARCHAR(20),
    ADD COLUMN IF NOT EXISTS payment_amount VARCHAR(20),
    ADD COLUMN IF NOT EXISTS payment_id     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS description    TEXT;

CREATE INDEX IF NOT EXISTS idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payments_payment_id ON payments(payment_id) WHERE payment_id IS NOT NULL;
