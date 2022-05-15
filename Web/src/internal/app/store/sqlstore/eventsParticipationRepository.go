package sqlstore

import (
	"bgf/internal/app/store"
	"bgf/internal/app/store/queries/eventParticipationQueries"
	"database/sql"
)

type EventsParticipationRepository struct {
	store *Store
}

// MakeParticipationInEvent Add participation in particular event
func (r *EventsParticipationRepository) MakeParticipationInEvent(eventId int, userId int) error {
	// Try to update first ...
	updQ := eventParticipationQueries.UpdateToActiveQuery
	var participationId int
	err := r.store.db.QueryRow(
		updQ,
		eventId,
		userId,
	).Scan(&participationId)

	if err != sql.ErrNoRows {
		return err
	}

	// err == sql.ErrNoRows -> create new entity
	insQ := eventParticipationQueries.InsertParticipationQuery
	_, err = r.store.db.Query(
		insQ,
		eventId,
		userId,
	)
	return err
}

// RemoveParticipationInEvent Removes participation in particular event
func (r *EventsParticipationRepository) RemoveParticipationInEvent(eventId int, userId int) error {

	updQ := eventParticipationQueries.UpdateToInactiveQuery
	var participationId int
	err := r.store.db.QueryRow(
		updQ,
		eventId,
		userId,
	).Scan(&participationId)

	if err == sql.ErrNoRows {
		return store.ErrRecordNotFound
	}
	return err
}
