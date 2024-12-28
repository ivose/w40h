-- Create reaction categories table
CREATE TABLE IF NOT EXISTS reactioncategories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    icon VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Add comments for reaction categories
COMMENT ON TABLE reactioncategories IS 'Different types of reactions available';
COMMENT ON COLUMN reactioncategories.name IS 'Name of the reaction type (reaction, love, angry, etc.)';
COMMENT ON COLUMN reactioncategories.icon IS 'Icon or emoji representing the reaction';

-- Seed reaction types
INSERT INTO reactioncategories (name, icon) VALUES
    ('like', '👍'),
    ('love', '❤️'),
    ('laugh', '😄'),
    ('angry', '😠'),
    ('sad', '😢'),
    ('dislike', '👎');

-- Add category column to reactions table
ALTER TABLE reactions
    ADD COLUMN category_id BIGINT;

-- Initially set all existing reactions to 'like' category
UPDATE reactions
SET category_id = (SELECT id FROM reactioncategories WHERE name = 'like');

-- Now make the column NOT NULL
ALTER TABLE reactions
    ALTER COLUMN category_id SET NOT NULL;

-- Add foreign key constraint
ALTER TABLE reactions
    ADD CONSTRAINT fk_reactions_category
        FOREIGN KEY (category_id) REFERENCES reactioncategories (id)
      ON DELETE RESTRICT;

-- Drop old unique constraint and create new one including category
DROP INDEX IF EXISTS uq_reactions_user_post;
CREATE UNIQUE INDEX uq_reactions_user_post_category
    ON reactions (user_id, post_id, category_id);

-- Add comment for the new column and constraint
COMMENT ON COLUMN reactions.category_id IS 'Reference to the type of reaction';
COMMENT ON CONSTRAINT fk_reactions_category ON reactions IS 'Ensures reaction type exists';

-- Add new example reactions with categories
INSERT INTO reactions (user_id, post_id, category_id)
VALUES
    (
        (SELECT id FROM users WHERE username='charlie'),
        (SELECT id FROM posts WHERE content='Bob''s first post'),
        (SELECT id FROM reactioncategories WHERE name='love')
    ),
    (
        (SELECT id FROM users WHERE username='alice'),
        (SELECT id FROM posts WHERE content='Charlie says hello'),
        (SELECT id FROM reactioncategories WHERE name='laugh')
    );