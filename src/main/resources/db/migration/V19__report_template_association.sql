ALTER TABLE reports
    ADD COLUMN IF NOT EXISTS template_id UUID REFERENCES setting_report_templates(id);

INSERT INTO setting_apps (key, value) VALUES
    ('entete',               ''),
    ('report_footer',        ''),
    ('report_review_title',  'Relu par'),
    ('impression_file_name', 'rapport')
ON CONFLICT (key) DO NOTHING;
