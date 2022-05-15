package utils

import (
	"crypto/tls"
	"fmt"
	"net"
	"net/mail"
	"net/smtp"
)

var mailClient smtp.Auth = nil

func SendMail(toEmail string, subj string, body string) error {

	from := mail.Address{Name: "BGF", Address: "no-reply@ij.je"}
	to := mail.Address{Name: "", Address: toEmail}

	headers := make(map[string]string)
	headers["From"] = from.String()
	headers["To"] = to.String()
	headers["Subject"] = subj
	headers["Content-Type"] = "text/html; charset=\"UTF-8\""

	message := ""
	for k, v := range headers {
		message += fmt.Sprintf("%s: %s\r\n", k, v)
	}
	message += "\r\n" + body

	servername := "smtp.yandex.ru:465"

	host, _, _ := net.SplitHostPort(servername)

	tlsconfig := &tls.Config{
		InsecureSkipVerify: true,
		ServerName:         host,
	}

	conn, err := tls.Dial("tcp", servername, tlsconfig)
	if err != nil {
		return err
	}

	a, err := smtp.NewClient(conn, host)
	if err != nil {
		return err
	}

	// Auth
	if err = a.Auth(mailClient); err != nil {
		return err
	}

	// To && From
	if err = a.Mail(from.Address); err != nil {
		return err
	}

	if err = a.Rcpt(to.Address); err != nil {
		return err
	}

	// Data
	w, err := a.Data()
	if err != nil {
		return err
	}

	_, err = w.Write([]byte(message))
	if err != nil {
		return err
	}

	err = w.Close()
	if err != nil {
		return err
	}

	a.Quit()

	return nil
}

func ConfigureMailClient(user string, password string) {
	mailClient = smtp.PlainAuth("", user, password, "smtp.yandex.ru")
}
