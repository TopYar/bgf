package sqlstore

import (
	"bgf/internal/app/model"
	"bgf/internal/app/store"
	"database/sql"
)

type ConfirmationCodeRepository struct {
	store *Store
}

// Create code
func (self *ConfirmationCodeRepository) Create(code *model.ConfirmationCode) error {
	return self.store.db.QueryRow(
		"INSERT INTO confirmation_codes (code, user_id, expires_at) VALUES ($1, $2, $3) RETURNING id",
		code.Code,
		code.UserId,
		code.ExpiresAt,
	).Scan(&code.Id)
}

// Search user by Id
func (self *ConfirmationCodeRepository) FindByUserId(userId string) (*model.ConfirmationCode, error) {
	code := &model.ConfirmationCode{}
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
func (self *ConfirmationCodeRepository) DeleteAllUserCodes(userId string) (interface{}, error) {
	Row := self.store.db.QueryRow(
		"DELETE FROM confirmation_codes WHERE user_id = $1",
		userId,
	)

	if Row.Err() != nil {
		return nil, Row.Err()
	}

	return nil, nil
}
