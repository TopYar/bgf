ALTER TABLE users
ALTER COLUMN nickname DROP NOT NULL;

ALTER TABLE users
    ALTER nickname SET default null;