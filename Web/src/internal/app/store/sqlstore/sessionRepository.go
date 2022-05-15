package sqlstore

import (
	"bgf/internal/app/models"
	"bgf/internal/app/store"
	"database/sql"
)

type SessionRepository struct {
	store *Store
}

// New Creates empty session for user
func (s *SessionRepository) New(userId int) (*models.Session, error) {
	session := &models.Session{
		UserId: userId,
		Values: models.Values{},
	}

	// Generate id
	if err := session.BeforeCreate(); err != nil {
		return nil, err
	}

	if err := s.store.db.QueryRow(
		"INSERT INTO sessions (id, values, user_id) VALUES ($1, $2, $3) RETURNING created_at",
		session.Id,
		session.Values,
		userId,
	).Scan(&session.CreatedAt); err != nil {
		return nil, err
	}

	return session, nil
}

// FindById Search session by id
func (s *SessionRepository) FindById(id string) (*models.Session, error) {
	session := &models.Session{
		Id: id,
	}

	if err := s.store.db.QueryRow(
		"SELECT values, user_id, expires_at, created_at, deleted_at FROM sessions WHERE id = $1",
		session.Id,
	).Scan(
		&session.Values,
		&session.UserId,
		&session.ExpiresAt,
		&session.CreatedAt,
		&session.DeletedAt,
	); err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	return session, nil
}

// FindAllByUserId Search sessions by user id
func (s *SessionRepository) FindAllByUserId(userId int) ([]*models.Session, error) {
	rows, err := s.store.db.Query(
		"SELECT id, values, user_id, expires_at, created_at, deleted_at FROM sessions WHERE user_id = $1",
		userId,
	)

	if err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	sessions := make([]*models.Session, 0)
	for rows.Next() {
		session := &models.Session{}

		err = rows.Scan(
			&session.Id,
			&session.Values,
			&session.UserId,
			&session.ExpiresAt,
			&session.CreatedAt,
			&session.DeletedAt,
		)
		if err != nil {
			return nil, err
		}

		sessions = append(sessions, session)
	}

	return sessions, nil
}

// RevokeSession Revoke session by id
func (s *SessionRepository) RevokeSession(id string) error {
	session, err := s.FindById(id)

	if err != nil {
		return err
	}

	// Means session.DeletedAt != null, so it was already deleted
	if session.DeletedAt.Valid {
		return nil
	}

	_, err = s.store.db.Query(
		"UPDATE sessions SET deleted_at = NOW() WHERE id = $1 and deleted_at IS NULL",
		session.Id,
	)

	if err != nil {
		return err
	}

	return nil
}

// RevokeAllSessionsExceptCurrent Revoke all sessions by user id, except for current session
func (s *SessionRepository) RevokeAllSessionsExceptCurrent(userId int, currentSessionId string) error {
	_, err := s.store.db.Query(
		"UPDATE sessions SET deleted_at = NOW() WHERE user_id = $1 and deleted_at IS NULL and id != $2",
		userId,
		currentSessionId,
	)

	if err != nil {
		return err
	}

	return nil
}

func (s *SessionRepository) RevokeAllSessions(userId int) error {
	_, err := s.store.db.Query(
		"UPDATE sessions SET deleted_at = NOW() WHERE user_id = $1 and deleted_at IS NULL",
		userId,
	)

	if err != nil {
		return err
	}

	return nil
}
