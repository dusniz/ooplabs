CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role user_role NOT NULL DEFAULT 'USER'
);

CREATE TABLE functions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT REFERENCES users(id)
    name VARCHAR(50) NOT NULL
    description VARCHAR(200)
    type VARCHAR(30) NOT NULL
    point_count INT,
    function_class VARCHAR(50)
);

CREATE TABLE points (
    id INT PRIMARY KEY AUTO_INCREMENT,
    function_id INT REFERENCES functions(id)
    x DOUBLE NOT NULL
    y DOUBLE NOT NULL
    index INT NOT NULL
);