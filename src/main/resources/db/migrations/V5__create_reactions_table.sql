-- Create base reactions table
CREATE TABLE IF NOT EXISTS reactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_reactions_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

COMMENT ON TABLE reactions IS 'Tracks user reactions on posts';
COMMENT ON COLUMN reactions.id IS 'Unique identifier for the like';
COMMENT ON COLUMN reactions.user_id IS 'Reference to the user who made the like';
COMMENT ON COLUMN reactions.post_id IS 'Reference to the post being liked';
COMMENT ON COLUMN reactions.created_at IS 'Timestamp when the like was created';
COMMENT ON COLUMN reactions.updated_at IS 'Timestamp when the like was last updated';

-- Initial indexes
CREATE UNIQUE INDEX IF NOT EXISTS uq_reactions_user_post ON reactions (user_id, post_id);
CREATE INDEX IF NOT EXISTS idx_reactions_user_id ON reactions (user_id);
CREATE INDEX IF NOT EXISTS idx_reactions_post_id ON reactions (post_id);

COMMENT ON INDEX uq_reactions_user_post IS 'Prevents users from liking the same post multiple times';
COMMENT ON INDEX idx_reactions_user_id IS 'Index for faster lookup of reactions by user';
COMMENT ON INDEX idx_reactions_post_id IS 'Index for faster lookup of reactions on a post';
COMMENT ON CONSTRAINT fk_reactions_user ON reactions IS 'Ensures user exists and handles cascade delete';
COMMENT ON CONSTRAINT fk_reactions_post ON reactions IS 'Ensures post exists and handles cascade delete';

-- Initial seed data
INSERT INTO reactions (user_id, post_id)
VALUES
    (
        (SELECT id FROM users WHERE username='alice'),
        (SELECT id FROM posts WHERE content='Bob''s first post')
    ),
    (
        (SELECT id FROM users WHERE username='bob'),
        (SELECT id FROM posts WHERE content='Alice first post')
    ),
    (
        (SELECT id FROM users WHERE username='charlie'),
        (SELECT id FROM posts WHERE content='Alice first post')
    );