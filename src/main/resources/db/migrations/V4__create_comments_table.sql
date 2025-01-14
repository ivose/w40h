-- Create comments table
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

COMMENT ON TABLE comments IS 'Stores user comments on posts';
COMMENT ON COLUMN comments.id IS 'Unique identifier for the comment';
COMMENT ON COLUMN comments.user_id IS 'Reference to the user who made the comment';
COMMENT ON COLUMN comments.post_id IS 'Reference to the post being commented on';
COMMENT ON COLUMN comments.content IS 'The actual content of the comment';
COMMENT ON COLUMN comments.created_at IS 'Timestamp when the comment was created';

-- Index for looking up comments by user or post
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments (user_id);
CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments (post_id);

COMMENT ON INDEX idx_comments_user_id IS 'Index for faster lookup of comments by user';
COMMENT ON INDEX idx_comments_post_id IS 'Index for faster lookup of comments on a post';
COMMENT ON CONSTRAINT fk_comments_user ON comments IS 'Ensures user exists and handles cascade delete';
COMMENT ON CONSTRAINT fk_comments_post ON comments IS 'Ensures post exists and handles cascade delete';


-- Seed data
INSERT INTO comments (user_id, post_id, content)
SELECT
    u.id as user_id,
    p.id as post_id,
    CASE
        WHEN u.username = 'bob' THEN 'Nice post, Alice!'
        WHEN u.username = 'alice' THEN 'Thanks Bob!'
        WHEN u.username = 'charlie' THEN 'Good stuff.'
        END as content
FROM users u
         CROSS JOIN posts p
WHERE
    (u.username = 'bob' AND p.content = 'Alice first post') OR
    (u.username = 'alice' AND p.content = 'Bob''s first post') OR
    (u.username = 'charlie' AND p.content = 'Alice second post');