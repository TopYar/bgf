package eventLikeQueries

var (
	InsertLikeQuery = `
		INSERT into events_likes (
			event_id,
			user_id
		)
		VALUES ( $1, $2 )
	`
)
