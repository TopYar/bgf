package sqlstore

import (
	"bgf/internal/app/models"
	"bgf/internal/app/models/requestDTO"
	"bgf/internal/app/store"
	"bgf/internal/app/store/queries/eventParticipationQueries"
	"bgf/internal/app/store/queries/eventQueries"
	"database/sql"
)

type EventsRepository struct {
	store *Store
}

func (r *EventsRepository) Create(userId int, dto *requestDTO.CreateEventDTO) error {
	query := eventQueries.InsertEventQuery

	return r.store.db.QueryRow(
		query,
		dto.Title,
		dto.Description,
		dto.EventDate,
		dto.VisitorsLimit,
		userId,
		dto.Location,
		dto.Latitude,
		dto.Longitude,
	).Scan(&dto.Id)
}

// Get all events ...
func (r *EventsRepository) Get(userId int, offset int, limit int) ([]*models.Event, error) {
	return r.getEventsList(
		eventQueries.SelectAllEventsQuery,
		userId,
		offset,
		limit,
	)
}
func (r *EventsRepository) GetOne(userId int, eventId int) (*models.Event, error) {
	return r.getOneEvent(
		eventQueries.SelectOneEventQuery,
		userId,
		eventId,
	)
}

// Get events liked by user ...
func (r *EventsRepository) GetLiked(userId int, offset int, limit int) ([]*models.Event, error) {
	return r.getEventsList(
		eventQueries.SelectAllLikedEventsQuery,
		userId,
		offset,
		limit,
	)
}

// Get event visitors ...
func (r *EventsRepository) GetVisitors(eventId int) ([]*models.User, error) {
	return r.getUsersList(
		eventParticipationQueries.GetVisitorsQuery,
		eventId,
	)
}

// Get events participated by user ...
func (r *EventsRepository) GetParticipated(userId int, offset int, limit int) ([]*models.Event, error) {
	return r.getEventsList(
		eventQueries.SelectAllParticipatedEventsQuery,
		userId,
		offset,
		limit,
	)
}

// Get events created by user ...
func (r *EventsRepository) GetCreated(userId int, offset int, limit int) ([]*models.Event, error) {
	return r.getEventsList(
		eventQueries.SelectAllCreatedEventsQuery,
		userId,
		offset,
		limit,
	)
}

// Private ...
func (r *EventsRepository) getEventsList(query string, userId int, offset int, limit int) ([]*models.Event, error) {
	rows, err := r.store.db.Query(query, userId, offset, limit)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	events := make([]*models.Event, 0)
	for rows.Next() {
		event, err := r.scanEventFromRow(rows)
		if err != nil {
			return nil, err
		}

		events = append(events, event)
	}

	return events, nil
}

func (r *EventsRepository) getOneEvent(query string, userId int, eventId int) (*models.Event, error) {
	rows, err := r.store.db.Query(query, userId, eventId)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	for rows.Next() {
		event, err := r.scanEventFromRow(rows)
		if err != nil {
			return nil, err
		}

		return event, nil
	}

	return nil, store.ErrRecordNotFound
}

func (r *EventsRepository) scanEventFromRow(row *sql.Rows) (*models.Event, error) {
	event := &models.Event{}
	if err := row.Scan(
		&event.Id,
		&event.Title,
		&event.Description,
		&event.ImageUrl,
		&event.CreateDate,
		&event.EventDate,
		&event.VisitorsLimit,
		&event.VisitorsCount,
		&event.Likes,
		&event.SubscriptionStatus,
		&event.Liked,
		&event.IsCreator,
		&event.Location,
		&event.Latitude,
		&event.Longitude,

		&event.Creator.Id,
		&event.Creator.Email,
		&event.Creator.Name,
		&event.Creator.Nickname,
		&event.Creator.City,
		&event.Creator.Country,
		&event.Creator.Rating,
		&event.Creator.ImageUrl,
		&event.Creator.SubscribersCount,
		&event.Creator.SubscriptionsCount,
		&event.Creator.GamesCount,
	); err != nil {
		return nil, err
	}

	return event, nil
}

// Private ...
func (r *EventsRepository) getUsersList(query string, eventId int) ([]*models.User, error) {
	rows, err := r.store.db.Query(query, eventId)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	users := make([]*models.User, 0)
	for rows.Next() {
		event, err := r.scanUserFromRow(rows)
		if err != nil {
			return nil, err
		}

		users = append(users, event)
	}

	return users, nil
}

func (r *EventsRepository) scanUserFromRow(row *sql.Rows) (*models.User, error) {
	user := &models.User{}
	if err := row.Scan(
		&user.Id,
		&user.Email,
		&user.Name,
		&user.Nickname,
		&user.City,
		&user.Country,
		&user.Rating,
		&user.ImageUrl,
		&user.SubscribersCount,
		&user.SubscriptionsCount,
		&user.GamesCount,
		&user.IsCreator,
	); err != nil {
		return nil, err
	}

	return user, nil
}
