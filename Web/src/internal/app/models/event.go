package models

import (
	"database/sql"
	"encoding/json"
	"time"

	validation "github.com/go-ozzo/ozzo-validation"
)

type NullFloat64 struct {
	sql.NullFloat64
}

func (v NullFloat64) MarshalJSON() ([]byte, error) {
	if v.Valid {
		return json.Marshal(v.Float64)
	} else {
		return json.Marshal(nil)
	}
}

type Event struct {
	Id                 int         `json:"id"`
	Title              string      `json:"title"`
	Description        string      `json:"description,omitempty"`
	ImageUrl           string      `json:"imageUrl,omitempty"`
	EventDate          time.Time   `json:"eventDate"`
	CreateDate         time.Time   `json:"createDate"`
	VisitorsLimit      int         `json:"visitorsLimit"`
	VisitorsCount      int         `json:"visitorsCount"`
	Likes              int         `json:"likes"`
	SubscriptionStatus string      `json:"subscriptionStatus"`
	Liked              bool        `json:"liked"`
	Creator            User        `json:"creator"`
	Visitors           []User      `json:"visitors"`
	IsCreator          bool        `json:"isCreator"`
	Location           string      `json:"location,omitempty"`
	Latitude           NullFloat64 `json:"latitude"`
	Longitude          NullFloat64 `json:"longitude"`
}

func (e *Event) Validate() error {
	return validation.ValidateStruct(
		e,
		validation.Field(&e.Title, validation.Required),
		validation.Field(&e.EventDate, validation.Required),
	)
}
