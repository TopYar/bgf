ALTER TABLE sessions
    ALTER created_at TYPE timestamp with time zone,
    ALTER deleted_at TYPE timestamp with time zone,
    ALTER expires_at TYPE timestamp with time zone;

ALTER TABLE confirmation_codes
    ALTER expires_at TYPE timestamp with time zone;

ALTER TABLE events
    ALTER create_date TYPE timestamp with time zone,
    ALTER event_date TYPE timestamp with time zone;

ALTER TABLE events_participation
    ALTER create_date TYPE timestamp with time zone,
    ALTER update_date TYPE timestamp with time zone;

ALTER TABLE users_subscriptions
    ALTER create_date TYPE timestamp with time zone,
    ALTER update_date TYPE timestamp with time zone;