package sqlstore

import (
	"database/sql"

	_ "github.com/lib/pq"
)

type Store struct {
	db                         *sql.DB
	userRepository             *UserRepository
	confirmationCodeRepository *ConfirmationCodeRepository
}

func New(db *sql.DB) *Store {
	return &Store{
		db: db,
	}
}

func (self *Store) UserRepo() *UserRepository {
	if self.userRepository == nil {
		self.userRepository = &UserRepository{
			store: self,
		}
	}

	return self.userRepository
}

func (self *Store) CofirmationCodeRepo() *ConfirmationCodeRepository {
	if self.confirmationCodeRepository == nil {
		self.confirmationCodeRepository = &ConfirmationCodeRepository{
			store: self,
		}
	}

	return self.confirmationCodeRepository
}
