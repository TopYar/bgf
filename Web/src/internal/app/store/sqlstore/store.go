package sqlstore

import (
	"database/sql"

	_ "github.com/lib/pq"
)

type Store struct {
	db                            *sql.DB
	userRepository                *UserRepository
	confirmationCodeRepository    *ConfirmationCodeRepository
	eventsRepository              *EventsRepository
	sessionRepository             *SessionRepository
	eventsLikesRepository         *EventsLikesRepository
	subscriptionsRepository       *UsersSubscriptionsRepository
	eventsParticipationRepository *EventsParticipationRepository
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

func (self *Store) EventsRepo() *EventsRepository {
	if self.eventsRepository == nil {
		self.eventsRepository = &EventsRepository{
			store: self,
		}
	}

	return self.eventsRepository
}

func (self *Store) SessionRepo() *SessionRepository {
	if self.sessionRepository == nil {
		self.sessionRepository = &SessionRepository{
			store: self,
		}
	}

	return self.sessionRepository
}

func (s *Store) EventsLikesRepo() *EventsLikesRepository {
	if s.eventsLikesRepository == nil {
		s.eventsLikesRepository = &EventsLikesRepository{
			store: s,
		}
	}

	return s.eventsLikesRepository
}

func (s *Store) SubscriptionsRepo() *UsersSubscriptionsRepository {
	if s.subscriptionsRepository == nil {
		s.subscriptionsRepository = &UsersSubscriptionsRepository{
			store: s,
		}
	}

	return s.subscriptionsRepository
}

func (s *Store) EventsParticipationRepo() *EventsParticipationRepository {
	if s.eventsParticipationRepository == nil {
		s.eventsParticipationRepository = &EventsParticipationRepository{
			store: s,
		}
	}

	return s.eventsParticipationRepository
}
