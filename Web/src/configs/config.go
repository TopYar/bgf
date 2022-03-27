package configs

import "time"

type Config struct {
	BindAddr               string `toml:"bind_addr"`
	DatabaseURL            string `toml:"database_url"`
	LogLevel               string `toml:"log_level"`
	JWTSecretKey           string `toml:"jwt_secret"`
	EmailUser              string `toml:"email_user"`
	EmailPassword          string `toml:"email_password"`

	AccessTokenExpiration  time.Duration
	RefreshTokenExpiration time.Duration
	ConfirmationCodeExpiration time.Duration
}

var ServerConfig = NewConfig()

func NewConfig() *Config {
	return &Config{
		BindAddr:               "",
		AccessTokenExpiration:  2 * time.Hour,
		RefreshTokenExpiration: 24 * time.Hour,
		ConfirmationCodeExpiration: 5 * time.Minute,
	}
}
