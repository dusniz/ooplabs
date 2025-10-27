-- USERS CRUD
-- CREATE
INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?);

-- READ
SELECT * FROM users WHERE id = ?;
SELECT * FROM users WHERE username = ?;

-- UPDATE
UPDATE users SET username = ?, password_hash = ?, email = ? WHERE id = ?;

-- DELETE
DELETE FROM users WHERE id = ?;

-- FUNCTIONS CRUD
-- CREATE
INSERT INTO functions (user_id, name, description, type, left_bound, right_bound, points_count, function_class)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

-- READ
SELECT * FROM functions WHERE id = ?;
SELECT * FROM functions WHERE user_id = ?;
SELECT * FROM functions WHERE user_id = ? AND type = ?;

-- UPDATE
UPDATE functions SET name = ?, description = ?, type = ?, left_bound = ?, right_bound = ?, points_count = ?, function_class = ?
WHERE id = ?;

-- DELETE
DELETE FROM functions WHERE id = ?;

-- POINTS CRUD
-- CREATE
INSERT INTO points (function_id, x, y, point_index) VALUES (?, ?, ?, ?);

-- READ
SELECT * FROM points WHERE function_id = ? ORDER BY point_index;
SELECT * FROM points WHERE function_id = ? AND point_index = ?;

-- UPDATE
UPDATE points SET x = ?, y = ? WHERE function_id = ? AND point_index = ?;

-- DELETE
DELETE FROM points WHERE function_id = ? AND point_index = ?;
DELETE FROM points WHERE function_id = ?;