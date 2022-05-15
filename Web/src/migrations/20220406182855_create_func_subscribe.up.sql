-- subscribe_to_user(subscriber_user, host_user)
CREATE OR REPLACE FUNCTION subscribe_to_user (integer, integer) RETURNS VOID AS $$
DECLARE
    is_subscribed boolean;
BEGIN 
    -- check if subscription row already exists
    SELECT is_active INTO is_subscribed
    FROM users_subscriptions
    WHERE subscriber_id = $1 AND host_id = $2;

    IF is_subscribed IS NULL THEN 
        -- add new subscription row
        INSERT INTO users_subscriptions (
            subscriber_id,
            host_id
        )
        VALUES ( $1, $2 );
    ELSE
        -- return if already subscribed
        IF is_subscribed THEN 
            RETURN;
        END IF;

        -- update existing subscription row
        UPDATE users_subscriptions
        SET 
            is_active = true,
            update_date = CURRENT_TIMESTAMP
        WHERE 
            subscriber_id = $1 AND
            host_id = $2;
    END IF;

    -- update subscription info for subscriber user along with host user
    UPDATE users SET subscribers_count = subscribers_count + 1 where id = $2;
    UPDATE users SET subscriptions_count = subscriptions_count + 1 where id = $1;
END
$$
LANGUAGE 'plpgsql';

