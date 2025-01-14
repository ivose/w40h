-- Create follows table (tracks follower -> followee)
CREATE TABLE IF NOT EXISTS follows (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    followee_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_followee FOREIGN KEY (followee_id) REFERENCES users (id) ON DELETE CASCADE,
    -- A user can't follow themselves; also avoid duplicates
    CONSTRAINT chk_follows_no_self_follow CHECK (follower_id <> followee_id)
);

COMMENT ON TABLE follows IS 'Tracks follower/followee relationships between users';
COMMENT ON COLUMN follows.id IS 'Unique identifier for the follow relationship';
COMMENT ON COLUMN follows.follower_id IS 'Reference to the user who is following';
COMMENT ON COLUMN follows.followee_id IS 'Reference to the user being followed';
COMMENT ON COLUMN follows.created_at IS 'Timestamp when the follow relationship was created';
COMMENT ON COLUMN follows.updated_at IS 'Timestamp when the follow relationship was last updated';


-- Avoid duplicate follow records: unique on pair (follower_id, followee_id)
CREATE UNIQUE INDEX IF NOT EXISTS uq_follows_pair ON follows (follower_id, followee_id);

-- Index for quickly finding followees of a user
CREATE INDEX IF NOT EXISTS idx_follows_follower ON follows (follower_id);

-- Index for quickly finding followers of a user
CREATE INDEX IF NOT EXISTS idx_follows_followee ON follows (followee_id);

COMMENT ON INDEX uq_follows_pair IS 'Prevents duplicate follow relationships';
COMMENT ON INDEX idx_follows_follower IS 'Index for faster lookup of who a user is following';
COMMENT ON INDEX idx_follows_followee IS 'Index for faster lookup of who follows a user';
COMMENT ON CONSTRAINT fk_follows_follower ON follows IS 'Ensures follower user exists and handles cascade delete';
COMMENT ON CONSTRAINT fk_follows_followee ON follows IS 'Ensures followee user exists and handles cascade delete';
COMMENT ON CONSTRAINT chk_follows_no_self_follow ON follows IS 'Prevents users from following themselves';


-- Seed data
INSERT INTO follows (follower_id, followee_id)
VALUES
    (
        (SELECT id FROM users WHERE username='alice'),
        (SELECT id FROM users WHERE username='bob')
    ),
    (
        (SELECT id FROM users WHERE username='bob'),
        (SELECT id FROM users WHERE username='alice')
    ),
    (
        (SELECT id FROM users WHERE username='charlie'),
        (SELECT id FROM users WHERE username='alice')
    );
