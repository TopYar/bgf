CREATE TABLE sessions(
    id varchar(64) not null primary key,
    user_id integer not null references users (id),
    values json not null,
    created_at timestamp not null default CURRENT_TIMESTAMP,
    expires_at timestamp,
    deleted_at timestamp
);