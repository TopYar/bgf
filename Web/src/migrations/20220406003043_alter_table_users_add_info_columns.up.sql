ALTER TABLE users
ADD COLUMN name varchar(128),
ADD COLUMN nickname varchar(128) not null default 'username',
ADD COLUMN city varchar(128),
ADD COLUMN country varchar(128),
ADD COLUMN rating numeric not null default 0,
ADD COLUMN image_url varchar,
ADD COLUMN subscribers_count int not null default 0,
ADD COLUMN subscriptions_count int not null default 0,
ADD COLUMN games_count int not null default 0;
