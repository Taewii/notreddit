-- seeding the user roles
INSERT INTO roles(authority)
VALUES ('ROOT'), ('ADMIN'), ('MODERATOR'), ('USER')
ON CONFLICT(authority) DO NOTHING;

-- disabling the foreign key constrains,
-- adding the default subreddits with dummy creator_id's
-- and enabling the constraints again
BEGIN;
    ALTER TABLE subreddits DISABLE TRIGGER ALL;
        INSERT INTO subreddits(title, creator_id)
        VALUES ('aww', '00000000-0000-0000-0000-000000000000'),
               ('HumansBeingBros', '00000000-0000-0000-0000-000000000000'),
               ('EyeBleach', '00000000-0000-0000-0000-000000000000')
        ON CONFLICT(title) DO NOTHING;
    ALTER TABLE subreddits ENABLE TRIGGER ALL;
COMMIT;
