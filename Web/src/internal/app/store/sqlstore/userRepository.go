package sqlstore

import (
	"bgf/internal/app/models"
	"bgf/internal/app/store"
	"bgf/internal/app/store/queries/usersSubscriptionsQueries"
	"database/sql"
)

type UserRepository struct {
	store *Store
}

// Creates user
func (self *UserRepository) Create(user *models.User) error {
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

func (self *UserRepository) UpdatePassword(user *models.User) error {
	if err := user.BeforeCreate(); err != nil {
		return err
	}

	return self.store.db.QueryRow(
		"UPDATE users SET encrypted_password = $2 WHERE id = $1 RETURNING id",
		user.Id,
		user.EncryptedPassword,
	).Scan(&user.Id)
}

// Search user by Id
func (self *UserRepository) FindById(id int) (*models.User, error) {
	user := &models.User{}
	if err := self.store.db.QueryRow(
		"SELECT id, email, nickname, encrypted_password FROM users WHERE id = $1",
		id,
	).Scan(
		&user.Id,
		&user.Email,
		&user.Nickname,
		&user.EncryptedPassword,
	); err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	return user, nil
}

// Search user by Id
func (self *UserRepository) FindByIdWithSubscriptions(userId int, id int) (*models.User, error) {
	user := &models.User{}
	if err := self.store.db.QueryRow(
		usersSubscriptionsQueries.GetUserWithSubscribeInfoQuery,
		userId,
		id,
	).Scan(
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
		&user.IsSubscription,
		&user.IsSubscribed,
		&user.IsMe,
	); err != nil {
		if err == sql.ErrNoRows {
			return nil, store.ErrRecordNotFound
		}
		return nil, err
	}

	return user, nil
}

// Search user by email
func (self *UserRepository) FindByEmail(email string) (*models.User, error) {
	user := &models.User{}
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

// Confirm user
func (self *UserRepository) ConfirmUser(user *models.User) error {
	if err := self.store.db.QueryRow(
		"UPDATE users SET confirmed_email = true WHERE id = $1 RETURNING confirmed_email",
		user.Id,
	).Scan(&user.ConfirmedEmail); err != nil {
		if err == sql.ErrNoRows {
			return store.ErrRecordNotFound
		}
		return err
	}

	return nil
}

func (r *UserRepository) SetDefaultNickname(user *models.User) error {
	if err := r.store.db.QueryRow(
		"UPDATE users SET nickname = 'user' || $1 WHERE id = $1 RETURNING nickname",
		user.Id,
	).Scan(&user.Nickname); err != nil {
		return err
	}

	return nil
}
