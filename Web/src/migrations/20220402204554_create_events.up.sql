CREATE TABLE events(
    id bigserial not null primary key,
    title varchar(255) not null,
    descr varchar(255),
    imageurl varchar(255),
    event_date timestamp not null,
    visitors_limit smallint not null,
    creator_id integer not null references users (id),
    likes integer not null default 0,
    location varchar(255),
    create_date timestamp not null default CURRENT_TIMESTAMP
);