CREATE TABLE confirmation_codes(
                      id bigserial not null primary key,
                      code varchar(32) not null,
                      user_id bigint REFERENCES users (id),
                      expires_at timestamp not null
);