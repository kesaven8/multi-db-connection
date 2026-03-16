\connect db2;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS invoice (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM invoice LIMIT 1) THEN
        BEGIN
            SET LOCAL synchronous_commit = OFF;

            INSERT INTO invoice (id, name, description)
            SELECT gen_random_uuid(),
                   'invoice-' || gs,
                   'generated during docker init'
            FROM generate_series(1, 1000000) AS gs;
        END;
    END IF;
END $$;

ANALYZE invoice;
