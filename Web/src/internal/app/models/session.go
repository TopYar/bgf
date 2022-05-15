package models

import (
	"bgf/utils"
	"database/sql"
	"database/sql/driver"
	"encoding/json"
	"errors"
	"time"
)

type Session struct {
	Id        string
	UserId    int
	Values    Values
	CreatedAt time.Time
	ExpiresAt sql.NullTime
	DeletedAt sql.NullTime
}

type Values struct {
	CountRequests int `json:"count_requests,omitempty"`
}

func (v Values) Value() (driver.Value, error) {
	return json.Marshal(v)
}

func (v *Values) Scan(value interface{}) error {
	b, ok := value.([]byte)
	if !ok {
		return errors.New("type assertion to []byte failed")
	}
	return json.Unmarshal(b, &v)
}

// Before create hook
func (s *Session) BeforeCreate() error {
	sessionId, err := utils.GenerateRandomString(32)

	if err != nil {
		return err
	}

	s.Id = sessionId

	return nil
}
