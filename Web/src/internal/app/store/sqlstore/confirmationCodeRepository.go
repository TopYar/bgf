package sqlstore

import (
	"bgf/internal/app/models"
	"bgf/internal/app/store"
	"database/sql"
)

type ConfirmationCodeRepository struct {
	store *Store
}

// Create code
func (self *ConfirmationCodeRepository) Create(code *models.ConfirmationCode) error {
	return self.store.db.QueryRow(
		"INSERT INTO confirmation_codes (code, user_id, expires_at) VALUES ($1, $2, $3) RETURNING id",
		code.Code,
		code.UserId,
		code.ExpiresAt,
	).Scan(&code.Id)
}

// Search user by Id
func (self *ConfirmationCodeRepository) FindByUserId(userId int) (*models.ConfirmationCode, error) {
	code := &models.ConfirmationCode{}
	if err := self.store.db.QueryRow(
		"SELECT id, code, user_id, expires_at FROM confirmation_codes WHERE user_id = $1",
		userId,
	).Scan(
		&code.Id,
		&code.Code,
		&code.UserId,
		&code.ExpiresAt,
	); err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	return code, nil
}

// Delete all user codes
func (self *ConfirmationCodeRepository) DeleteAllUserCodes(userId int) error {
	Row := self.store.db.QueryRow(
		"DELETE FROM confirmation_codes WHERE user_id = $1",
		userId,
	)

	if Row.Err() != nil {
		return Row.Err()
	}

	return nil
}
