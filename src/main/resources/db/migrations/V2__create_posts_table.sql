-- Create posts table
CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

COMMENT ON TABLE posts IS 'Stores user posts and their content';
COMMENT ON COLUMN posts.id IS 'Unique identifier for the post';
COMMENT ON COLUMN posts.user_id IS 'Reference to the user who created the post';
COMMENT ON COLUMN posts.title IS 'Title of the post';
COMMENT ON COLUMN posts.content IS 'The actual content of the post';
COMMENT ON COLUMN posts.created_at IS 'Timestamp when the post was created';
COMMENT ON COLUMN posts.updated_at IS 'Timestamp when the post was last updated';

-- Index on user_id if you often fetch posts for a specific user
CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts (user_id);

COMMENT ON INDEX idx_posts_user_id IS 'Index for faster lookup of posts by user';
COMMENT ON CONSTRAINT fk_posts_user ON posts IS 'Ensures user exists and handles cascade delete';

-- Seed data
INSERT INTO posts (user_id, title, content) VALUES
    ((SELECT id FROM users WHERE username='alice'), 'Hello', 'Alice first post'),
    ((SELECT id FROM users WHERE username='alice'), 'Hello again','Alice second post'),
    ((SELECT id FROM users WHERE username='bob'), 'Hello','Bob''s first post'),
    ((SELECT id FROM users WHERE username='charlie'),'Hello, here''s Charlie', 'Charlie says hello');
