package usersSubscriptionsQueries

var (
	GetSubscriptionsQuery = `
		SELECT 
			users.id,
			users.email,
			CASE WHEN sub_rev.subscriber_id is null
				THEN false
				ELSE true
			END as reversed_subscription
		FROM users_subscriptions sub
		LEFT JOIN users_subscriptions sub_rev ON 
			sub_rev.is_active AND
			sub_rev.host_id = $1 AND
			sub_rev.subscriber_id = sub.host_id
		JOIN users ON
			sub.host_id = users.id
		WHERE 
			sub.is_active AND
			sub.subscriber_id = $1
		ORDER BY update_date DESC
		OFFSET $2
		LIMIT $3;
	`

	GetSubscribersQuery = `
		SELECT
			users.id,
			users.email,
			CASE WHEN sub_rev.subscriber_id is null
				THEN false
				ELSE true
			END as reversed_subscription
		FROM users_subscriptions sub
		LEFT JOIN users_subscriptions sub_rev ON 
			sub_rev.is_active AND
			sub_rev.subscriber_id = $1 AND 
			sub_rev.host_id = sub.subscriber_id 
		JOIN users ON 
			sub.subscriber_id = users.id
		WHERE 
			sub.is_active AND 
			sub.host_id = $1
		ORDER BY update_date DESC
		OFFSET $2
		LIMIT $3;
	`

	GetUserWithSubscribeInfoQuery = `
	SELECT 
		users.id,
		users.email,
		coalesce(users.name, '') as name,
		users.nickname,
		coalesce(users.city, '') as city,
		coalesce(users.country, '') as country,
		users.rating,
		coalesce(users.image_url, '') as image_url,
		subscribers_count,
		subscriptions_count,
		0 as games_count,
		sub.id IS NOT NULL as is_subscription,
		sub2.id IS NOT NULL as is_subscribed,
		users.id = $1 as is_me
	FROM users
	LEFT JOIN users_subscriptions sub ON 
			sub.is_active AND
			sub.host_id = $2 AND
			sub.subscriber_id = $1
	LEFT JOIN users_subscriptions sub2 ON 
			sub2.is_active AND
			sub2.host_id = $1 AND
			sub2.subscriber_id = $2
	WHERE users.id = $2
`
)
