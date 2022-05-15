package configs

import "time"

type Config struct {
	BindAddr      string `toml:"bind_addr"`
	DatabaseURL   string `toml:"database_url"`
	LogLevel      string `toml:"log_level"`
	JWTSecretKey  string `toml:"jwt_secret"`
	SessionSecret string `toml:"session_secret"`
	EmailUser     string `toml:"email_user"`
	EmailPassword string `toml:"email_password"`
	BaseUrl       string `toml:"base_url"`
	VersionApi    string `toml:"version_api"`

	AccessTokenExpiration      time.Duration
	RefreshTokenExpiration     time.Duration
	ConfirmationCodeExpiration time.Duration
	RecoverPasswordExpiration  time.Duration
	SessionExpiration          time.Duration
}

var ServerConfig = NewConfig()

func NewConfig() *Config {
	return &Config{
		BindAddr:                   "",
		AccessTokenExpiration:      2 * time.Hour,
		RefreshTokenExpiration:     24 * 7 * time.Hour,
		ConfirmationCodeExpiration: 5 * time.Minute,
		RecoverPasswordExpiration:  15 * time.Minute,
		SessionExpiration:          2 * time.Hour,
	}
}
