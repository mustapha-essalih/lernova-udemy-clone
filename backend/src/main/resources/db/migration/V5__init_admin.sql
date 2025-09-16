INSERT INTO admin (username, password_hash, role)
VALUES (
    'admin',
    '$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXX', -- BCrypt hashed password
    'ADMIN'
);
