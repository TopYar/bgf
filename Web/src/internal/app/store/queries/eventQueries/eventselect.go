package eventQueries

var (

	// $1 - currentUserId, $2 - Offset, $3 - Limit
	commonQuery = `
		SELECT 
			-- Event info --
			events.id, 
			events.title,
			coalesce(events.descr, '') as descr,
			coalesce(events.imageurl, '') as imageurl,
			events.create_date, 
			events.event_date,
			events.visitors_limit,
			(
				SELECT COUNT(*) FROM events_participation e_p
				WHERE 
					e_p.is_active AND
					e_p.event_id = events.id
			) as visitors_count,
			(
				SELECT COUNT(*) FROM events_likes e_l
				WHERE 
					e_l.is_active AND
					e_l.event_id = events.id
			) as likes,
			(
				CASE WHEN particip.id IS NULL
					THEN 'not_submitted'
					ELSE 'accepted'
				END
			) as subscription_status,
			(
				CASE WHEN likes.id IS NULL
					THEN false
					ELSE true
				END
			) as liked,
			events.creator_id = $1 as is_creator,
			coalesce(events.location, '') as location,
			latitude,
			longitude,
			-- User Info --
			users.id,
			users.email,
			coalesce(users.name, '') as creator_name,
			users.nickname,
			coalesce(users.city, '') as creator_city,
			coalesce(users.country, '') as creator_country,
			users.rating,
			coalesce(users.image_url, '') as creator_image_url,
			0 as subscribers_count,
			0 as subscriptions_count,
			0 as games_count
		FROM 
			events
		JOIN users ON 
			events.creator_id = users.id 
		LEFT JOIN events_participation particip ON 
			particip.is_active AND
			particip.user_id = $1 AND
			events.id = particip.event_id
		LEFT JOIN events_likes likes ON 
			likes.is_active AND
			likes.user_id = $1 AND 
			events.id = likes.event_id
	`

	SelectAllEventsQuery = commonQuery + `
		ORDER BY create_date DESC
		OFFSET $2
		LIMIT $3;
	`

	SelectAllLikedEventsQuery = commonQuery + `
		JOIN events_likes e_l ON
			e_l.is_active AND
			e_l.user_id = $1 AND
			e_l.event_id = events.id
		ORDER BY create_date DESC
		OFFSET $2
		LIMIT $3;
	`

	SelectAllParticipatedEventsQuery = commonQuery + `
		JOIN events_participation e_p ON
			e_p.is_active AND
			e_p.user_id = $1 AND
			e_p.event_id = events.id
		ORDER BY create_date DESC
		OFFSET $2
		LIMIT $3;
	`

	SelectAllCreatedEventsQuery = commonQuery + `
		WHERE events.creator_id = $1
		ORDER BY create_date DESC
		OFFSET $2
		LIMIT $3;
	`

	SelectOneEventQuery = commonQuery + `
		WHERE events.id = $2
	`
)
