package sqlstore

import (
	"bgf/internal/app/model"
	"bgf/internal/app/store"
	"database/sql"
)

type UserRepository struct {
	store *Store
}

// Creates user
func (self *UserRepository) Create(user *model.User) error {
	if err := user.Validate(); err != nil {
		return err
	}

	if err := user.BeforeCreate(); err != nil {
		return err
	}

	return self.store.db.QueryRow(
		"INSERT INTO users (email, encrypted_password) VALUES ($1, $2) RETURNING id",
		user.Email,
		user.EncryptedPassword,
	).Scan(&user.Id)
}

// Search user by Id
func (self *UserRepository) FindById(id int) (*model.User, error) {
	user := &model.User{}
	if err := self.store.db.QueryRow(
		"SELECT id, email, encrypted_password FROM users WHERE id = $1",
		id,
	).Scan(
		&user.Id,
		&user.Email,
		&user.EncryptedPassword,
	); err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	return user, nil
}

// Search user by email
func (self *UserRepository) FindByEmail(email string) (*model.User, error) {
	user := &model.User{}
	if err := self.store.db.QueryRow(
		"SELECT id, email, encrypted_password FROM users WHERE email = $1",
		email,
	).Scan(
		&user.Id,
		&user.Email,
		&user.EncryptedPassword,
	); err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	return user, nil
}
