package eventQueries

var (
	InsertEventQuery = `
		INSERT INTO events (
			title, 
			descr, 
			event_date,
			visitors_limit, 
			creator_id, 
			location,
			latitude,
			longitude
		) 
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8) 
		RETURNING id;
	`
)
