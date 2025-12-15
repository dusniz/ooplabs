DROP TABLE IF EXISTS functions CASCADE;

CREATE TABLE functions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    type VARCHAR(30) NOT NULL,
    point_count INT,
    function_class VARCHAR(50)
);