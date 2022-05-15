package eventParticipationQueries

var (
	GetVisitorsQuery = `
				(SELECT
					users.id,
					users.email,
					coalesce(users.name, '') as name,
					users.nickname,
					coalesce(users.city, '') as city,
					coalesce(users.country, '') as country,
					users.rating,
					coalesce(users.image_url, '') as image_url,
					0 as subscribers_count,
					0 as subscriptions_count,
					0 as games_count,
					false as is_creator
				FROM events_participation e_p
						 JOIN users
							  ON e_p.user_id = users.id
				WHERE
					e_p.event_id = $1 AND
					e_p.is_active)
				UNION
				(SELECT
					users.id,
					users.email,
					coalesce(users.name, '') as name,
					users.nickname,
					coalesce(users.city, '') as city,
					coalesce(users.country, '') as country,
					users.rating,
					coalesce(users.image_url, '') as image_url,
					0 as subscribers_count,
					0 as subscriptions_count,
					0 as games_count,
					true as is_creator
				FROM events e
						 JOIN users
							  ON e.creator_id = users.id
				WHERE
					e.id = $1)
				ORDER BY is_creator DESC
				
	`
)
