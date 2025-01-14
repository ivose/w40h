-- Create base commentreactions table
CREATE TABLE IF NOT EXISTS commentreactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_commentreactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_commentreactions_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
);

COMMENT ON TABLE commentreactions IS 'Tracks user comment reactions on comments';
COMMENT ON COLUMN commentreactions.id IS 'Unique identifier for the like';
COMMENT ON COLUMN commentreactions.user_id IS 'Reference to the user who made the like';
COMMENT ON COLUMN commentreactions.comment_id IS 'Reference to the comment being liked';
COMMENT ON COLUMN commentreactions.created_at IS 'Timestamp when the like was created';
COMMENT ON COLUMN commentreactions.updated_at IS 'Timestamp when the like was last updated';

-- Initial indexes
CREATE UNIQUE INDEX IF NOT EXISTS uq_commentreactions_user_comment ON commentreactions (user_id, comment_id);
CREATE INDEX IF NOT EXISTS idx_commentreactions_user_id ON commentreactions (user_id);
CREATE INDEX IF NOT EXISTS idx_commentreactions_comment_id ON commentreactions (comment_id);

COMMENT ON INDEX uq_commentreactions_user_comment IS 'Prevents users from liking the same comment multiple times';
COMMENT ON INDEX idx_commentreactions_user_id IS 'Index for faster lookup of comment reactions by user';
COMMENT ON INDEX idx_commentreactions_comment_id IS 'Index for faster lookup of comment reactions on a comment';
COMMENT ON CONSTRAINT fk_commentreactions_user ON commentreactions IS 'Ensures user exists and handles cascade delete';
COMMENT ON CONSTRAINT fk_commentreactions_comment ON commentreactions IS 'Ensures comment exists and handles cascade delete';

