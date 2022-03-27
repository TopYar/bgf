package model

import (
	. "bgf/configs"
	"time"
)

type ConfirmationCode struct {
	Id        int       `json:"id"`
	UserId    int       `json:"user_id"`
	Code      string    `json:"code"`
	ExpiresAt time.Time `json:"expires_at"`
}

func CreateConfirmationCode(userId int, code string) *ConfirmationCode {
	expiresAt := time.Now().Add(ServerConfig.ConfirmationCodeExpiration)

	confirmationCode := &ConfirmationCode{
		UserId:    userId,
		Code:      code,
		ExpiresAt: expiresAt,
	}

	return confirmationCode
}
