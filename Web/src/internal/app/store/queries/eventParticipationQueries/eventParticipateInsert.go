package eventParticipationQueries

var (
	InsertParticipationQuery = `
		INSERT INTO events_participation (
			event_id,
			user_id
		)
		VALUES ( $1, $2 )
	`
)
