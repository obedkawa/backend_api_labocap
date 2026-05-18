-- Split users.name into firstname + lastname (Laravel source of truth)
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS firstname VARCHAR(200),
    ADD COLUMN IF NOT EXISTS lastname  VARCHAR(200);

UPDATE users
SET firstname = CASE
                    WHEN position(' ' IN name) > 0 THEN split_part(name, ' ', 1)
                    ELSE name
                END,
    lastname  = CASE
                    WHEN position(' ' IN name) > 0
                        THEN trim(substring(name FROM position(' ' IN name) + 1))
                    ELSE name
                END;

ALTER TABLE users
    ALTER COLUMN firstname SET NOT NULL,
    ALTER COLUMN lastname SET NOT NULL;

ALTER TABLE users
    DROP COLUMN name;
