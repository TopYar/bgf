package utils

import (
	"net/smtp"
	"strings"
)

var mailClient smtp.Auth = nil

func SendMail(to []string, subject string, body string) {

	msg := "To: " + strings.Join(to[:], ",") + "\r\n" +
		"Subject: " + subject + "\r\n" +
		"\r\n" +
		body + "\r\n"
	err := smtp.SendMail("smtp.yandex.ru:465", mailClient, "BGF <no-reply@ij.je>", to, []byte(msg))
	if err != nil {
		Logger.Error(err)
	}
}

func ConfigureMailClient(user string, password string) {
	mailClient = smtp.PlainAuth("", user, password, "smtp.yandex.ru")
}
