/**
 * Initial database schema for social network application
 * Tables: users, posts, follows, comments, likes
 */
--postgresql
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP,
    full_name VARCHAR(100) NOT NULL,
    born DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    is_client BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS 'Stores user accounts and their authentication details';
COMMENT ON COLUMN users.id IS 'Unique identifier for the user';
COMMENT ON COLUMN users.email IS 'User email address - must be unique';
COMMENT ON COLUMN users.username IS 'Username for login - must be unique';
COMMENT ON COLUMN users.password IS 'Hashed password for user authentication';
COMMENT ON COLUMN users.full_name IS 'Full name of the user';
COMMENT ON COLUMN users.born IS 'Date of birth of the user';
COMMENT ON COLUMN users.is_active IS 'Flag indicating if the account is active';
COMMENT ON COLUMN users.is_admin IS 'Flag indicating if user has administrator privileges';
COMMENT ON COLUMN users.is_client IS 'Flag indicating if user has client privileges';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user account was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user account was last updated';

-- Optional index on username if you often search by username
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
-- Optional index on username if you often search by username
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

COMMENT ON INDEX idx_users_username IS 'Index for faster username lookups';
COMMENT ON INDEX idx_users_email IS 'Index for faster email lookups';


INSERT INTO users (email, username, password, full_name, born, is_active, is_client, is_admin)
VALUES
    ('admin@example.com', 'admin', 'admin123', 'MyAdmin', '1990-01-01', TRUE, FALSE, TRUE),
    ('client@example.com', 'client', 'client123', 'MyClient', '1990-01-01', TRUE, TRUE, FALSE),
    ('test@example.com', 'test', 'test123', 'TestUser', '1990-01-01', TRUE, FALSE, FALSE),
    ('alice@example.com', 'alice', 'alice123', 'TestAlice', '1990-01-01', TRUE, FALSE, FALSE),
    ('bob@example.com', 'bob', 'bob123', 'TestBob', '1990-01-01', TRUE, FALSE, FALSE),
    ('charlie@example.com', 'charlie', 'charlie123', 'TestCharlie', '1990-01-01', TRUE, FALSE, FALSE)
ON CONFLICT (email) DO NOTHING;
-- how to do if user not exists then insert?
