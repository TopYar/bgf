package sqlstore

import (
	"bgf/internal/app/store"
	"bgf/internal/app/store/queries/eventLikeQueries"
	"database/sql"
)

type EventsLikesRepository struct {
	store *Store
}

func (r *EventsLikesRepository) LikeEvent(eventId int, userId int) error {
	// Try to update first ...
	updQ := eventLikeQueries.UpdateToActiveQuery
	var likeId string
	err := r.store.db.QueryRow(
		updQ,
		eventId,
		userId,
	).Scan(&likeId)

	// nil or anything but ErrNoRows found -> return err
	if err != sql.ErrNoRows {
		return err
	}

	// Try to create new entity ...
	insQ := eventLikeQueries.InsertLikeQuery
	_, err = r.store.db.Query(
		insQ,
		eventId,
		userId,
	)
	return err
}

func (r *EventsLikesRepository) RemoveLikeEvent(eventId int, userId int) error {
	updQ := eventLikeQueries.UpdateToInactiveQuery
	var likeId string
	err := r.store.db.QueryRow(
		updQ,
		eventId,
		userId,
	).Scan(&likeId)

	if err == sql.ErrNoRows {
		return store.ErrRecordNotFound
	}
	return err
}
