-- Add parent comment reference after post_id with default 0
ALTER TABLE comments
    ADD COLUMN parent_comment_id BIGINT AFTER post_id;

-- Add foreign key constraint
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_parent
        FOREIGN KEY (parent_comment_id)
            REFERENCES comments (id)
            ON DELETE CASCADE;

-- Add index for faster lookups
CREATE INDEX idx_comments_parent_id ON comments (parent_comment_id);

-- Add comment for new column and constraints
COMMENT ON COLUMN comments.parent_comment_id IS 'Reference to parent comment for nested replies';
COMMENT ON CONSTRAINT fk_comments_parent ON comments IS 'Ensures parent comment exists and handles cascade delete';
COMMENT ON INDEX idx_comments_parent_id IS 'Index for faster lookup of comment replies';