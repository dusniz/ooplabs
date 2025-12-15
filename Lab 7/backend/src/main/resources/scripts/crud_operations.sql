-- USERS
-- CREATE
INSERT INTO users (username, password_hash) VALUES (?, ?);

-- READ
SELECT * FROM users WHERE id = ?;
SELECT * FROM users WHERE username = ?;

-- UPDATE
UPDATE users SET username = ?, password_hash = ? WHERE id = ?;

-- DELETE
DELETE FROM users WHERE id = ?;

-- FUNCTIONS
-- CREATE
INSERT INTO functions (user_id, name, description, type, point_count, function_class)
VALUES (?, ?, ?, ?, ?, ?);

-- READ
SELECT * FROM functions WHERE id = ?;
SELECT * FROM functions WHERE user_id = ?;
SELECT * FROM functions WHERE user_id = ? AND type = ?;

-- UPDATE
UPDATE functions SET name = ?, description = ?, type = ?, point_count = ?, function_class = ?
WHERE id = ?;

-- DELETE
DELETE FROM functions WHERE id = ?;

-- POINTS
-- CREATE
INSERT INTO points (function_id, x, y, index) VALUES (?, ?, ?, ?);

-- READ
SELECT * FROM points WHERE function_id = ? ORDER BY index;
SELECT * FROM points WHERE function_id = ? AND index = ?;

-- UPDATE
UPDATE points SET x = ?, y = ? WHERE function_id = ? AND index = ?;

-- DELETE
DELETE FROM points WHERE function_id = ? AND index = ?;
DELETE FROM points WHERE function_id = ?;