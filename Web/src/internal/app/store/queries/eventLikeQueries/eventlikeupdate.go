package eventLikeQueries

var (
	UpdateToActiveQuery = `
		UPDATE events_likes 
		SET is_active = true 
		WHERE 
			event_id = $1 AND
			user_id = $2
		RETURNING id;
	`

	UpdateToInactiveQuery = `
		UPDATE events_likes 
		SET is_active = false 
		WHERE 
			event_id = $1 AND
			user_id = $2
		RETURNING id;
	`
)
