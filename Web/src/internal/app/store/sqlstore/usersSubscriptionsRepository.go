package sqlstore

import (
	"bgf/internal/app/models"
	"bgf/internal/app/store"
	"bgf/internal/app/store/queries/usersSubscriptionsQueries"
	"database/sql"
)

type UsersSubscriptionsRepository struct {
	store *Store
}

// SubscriberToUser Adds subscribtion to user
func (r *UsersSubscriptionsRepository) SubscribeToUser(userId int, currentUserId int) error {
	q := "SELECT subscribe_to_user($1, $2);"
	_, err := r.store.db.Query(
		q,
		currentUserId,
		userId,
	)
	return err
}

// UnsubscribeFromUser Removes subscribtion from user
func (r *UsersSubscriptionsRepository) UnsubscribeFromUser(userId int, currentUserId int) error {
	q := "SELECT unsubscribe_from_user($1, $2);"
	_, err := r.store.db.Query(
		q,
		currentUserId,
		userId,
	)
	return err
}

func (r *UsersSubscriptionsRepository) GetAllSubscribers(userId int, offset int, limit int) ([]*models.User, error) {
	q := usersSubscriptionsQueries.GetSubscribersQuery
	rows, err := r.store.db.Query(q, userId, offset, limit)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	users := make([]*models.User, 0)
	for rows.Next() {
		user, err := scanUserFromRow(rows)
		if err != nil {
			return nil, err
		}
		users = append(users, user)
	}

	return users, nil
}

func (r *UsersSubscriptionsRepository) GetAllSubscriptions(userId int, offset int, limit int) ([]*models.User, error) {
	q := usersSubscriptionsQueries.GetSubscriptionsQuery
	rows, err := r.store.db.Query(q, userId, offset, limit)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	users := make([]*models.User, 0)
	for rows.Next() {
		user, err := scanUserFromRow(rows)
		if err != nil {
			return nil, err
		}
		users = append(users, user)
	}

	return users, nil
}

func scanUserFromRow(row *sql.Rows) (*models.User, error) {
	user := &models.User{}
	if err := row.Scan(
		&user.Id,
		&user.Email,
	); err != nil {
		return nil, err
	}

	return user, nil
}
