package requestDTO

import (
	"time"

	validation "github.com/go-ozzo/ozzo-validation"
)

type CreateEventDTO struct {
	Id            int       `json:"-"`
	Title         string    `json:"title"`
	Description   string    `json:"description,omitempty"`
	VisitorsLimit int       `json:"visitorsLimit"`
	Location      string    `json:"location"`
	Latitude      float64   `json:"latitude"`
	Longitude     float64   `json:"longitude"`
	EventDate     time.Time `json:"eventDate"`
}

func (e *CreateEventDTO) Validate() error {
	return validation.ValidateStruct(
		e,
		validation.Field(&e.Title, validation.Required),
		validation.Field(&e.EventDate, validation.Required),
		validation.Field(&e.Latitude, validation.Required),
		validation.Field(&e.Longitude, validation.Required),
	)
}
