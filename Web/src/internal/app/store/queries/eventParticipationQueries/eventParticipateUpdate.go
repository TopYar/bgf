package eventParticipationQueries

var (
	UpdateToActiveQuery = `
		UPDATE events_participation
		SET 
			is_active = true,
			update_date = CURRENT_TIMESTAMP
		WHERE 
			event_id = $1 AND
			user_id = $2
		RETURNING id;
	`

	UpdateToInactiveQuery = `
		UPDATE events_participation
		SET 
			is_active = false,
			update_date = CURRENT_TIMESTAMP
		WHERE 
			event_id = $1 AND
			user_id = $2
		RETURNING id;
	`
)
