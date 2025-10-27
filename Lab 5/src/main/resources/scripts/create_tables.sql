DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
    END IF;
END$$;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS functions CASCADE;
DROP TABLE IF EXISTS points CASCADE;

CREATE TABLE users (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role user_role NOT NULL DEFAULT 'USER'
);

CREATE TABLE functions (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INT REFERENCES users(id),
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    type VARCHAR(30) NOT NULL,
    point_count INT,
    function_class VARCHAR(50)
);

CREATE TABLE points (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    function_id INT REFERENCES functions(id),
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    index INT NOT NULL
);