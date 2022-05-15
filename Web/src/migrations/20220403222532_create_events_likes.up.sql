CREATE TABLE events_likes(
    id bigserial not null primary key,
    event_id integer not null references events (id),
    user_id integer not null references users (id),
    is_active boolean not null default true
);