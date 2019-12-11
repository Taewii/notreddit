-- seeding the user roles
INSERT INTO roles(authority)
VALUES ('ROOT'), ('ADMIN'), ('MODERATOR'), ('USER')
ON CONFLICT(authority) DO NOTHING;

-- seeding the default subreddits
INSERT INTO subreddits(title, creator_id)
VALUES ('aww', null),
       ('HumansBeingBros', null),
       ('EyeBleach', null)
ON CONFLICT(title) DO NOTHING;