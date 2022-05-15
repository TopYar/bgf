CREATE TABLE users_subscriptions(
    id bigserial not null primary key,
    subscriber_id integer not null references users (id),
    host_id integer not null references users (id),
    is_active boolean not null default true,
    create_date timestamp not null default CURRENT_TIMESTAMP,
    update_date timestamp not null default CURRENT_TIMESTAMP
);