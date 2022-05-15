package models

import (
	"bgf/utils"

	validation "github.com/go-ozzo/ozzo-validation"
	"github.com/go-ozzo/ozzo-validation/is"
)

type User struct {
	Id                 int     `json:"id"`
	Email              string  `json:"email"`
	Name               string  `json:"name,omitempty"`
	Nickname           string  `json:"nickname"`
	City               string  `json:"city,omitempty"`
	Country            string  `json:"country,omitempty"`
	Rating             float64 `json:"rating"`
	ImageUrl           string  `json:"imageUrl,omitempty"`
	SubscribersCount   int     `json:"subscribersCount"`
	SubscriptionsCount int     `json:"subscriptionsCount"`
	GamesCount         int     `json:"gamesCount"`
	Password           string  `json:"password,omitempty"`

	ConfirmedEmail    bool   `json:"-"`
	EncryptedPassword string `json:"-"`
	IsCreator         bool   `json:"-"`
	IsSubscription    bool   `json:"isSubscription"`
	IsSubscribed      bool   `json:"isSubscribed"`
	IsMe              bool   `json:"isMe"`
}

// User validation
func (self *User) Validate() error {
	isNewUser := self.EncryptedPassword == ""
	passwordValidationRule := utils.RequiredIf(isNewUser)
	return validation.ValidateStruct(
		self,
		validation.Field(&self.Email, validation.Required, is.Email),
		validation.Field(&self.Password, validation.By(passwordValidationRule), validation.Length(4, 80)),
	)
}

// Before create hook
func (self *User) BeforeCreate() error {
	if len(self.Password) > 0 {
		encryptedString, err := utils.EncryptString(self.Password)
		if err != nil {
			return err
		}
		self.EncryptedPassword = encryptedString
	}
	return nil
}

// Sanitizes user credentials
func (self *User) Sanitize() {
	self.Password = ""
}

// Compares given password to existing encrypted password
func (self *User) PasswordEqualTo(password string) bool {
	return utils.ComparePasswords(self.EncryptedPassword, password)
}
