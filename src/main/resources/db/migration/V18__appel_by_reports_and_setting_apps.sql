CREATE TABLE IF NOT EXISTS appel_by_reports (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id  UUID        NOT NULL,
    report_id  UUID        NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    appel_id   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_appel_by_reports_report ON appel_by_reports(report_id);

CREATE TABLE IF NOT EXISTS setting_apps (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    key        VARCHAR(100) NOT NULL,
    value      TEXT,
    CONSTRAINT uq_setting_apps_key UNIQUE (key)
);

INSERT INTO setting_apps (key, value) VALUES
    ('api_key_ourvoice',  ''),
    ('link_ourvoice_call', 'https://api.getourvoice.com/v1/calls'),
    ('link_ourvoice_sms',  'https://api.getourvoice.com/v1/messages')
ON CONFLICT (key) DO NOTHING;
