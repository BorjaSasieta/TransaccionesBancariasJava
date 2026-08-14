DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'bf_user') THEN
            CREATE USER bf_user WITH PASSWORD 'bf_pass';
        END IF;
    END
$$;

-- crea bd si no existe y asigna propietario
SELECT 'CREATE DATABASE bankflow OWNER bf_user'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'bankflow')
\gexec

GRANT ALL PRIVILEGES ON DATABASE bankflow TO bf_user;