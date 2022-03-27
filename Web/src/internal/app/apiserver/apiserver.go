package apiserver

import (
	. "bgf/configs"
	"bgf/internal/app/store/sqlstore"
	. "bgf/utils"
	"database/sql"
	"net/http"
)

func Start() error {
	// Setup logger
	Logger = NewLogger(ServerConfig.LogLevel)

	// Setup database
	db, err := newDatabase(ServerConfig.DatabaseURL)
	if err != nil {
		return err
	}
	defer db.Close()

	// Configure mail client
	ConfigureMailClient(ServerConfig.EmailUser, ServerConfig.EmailPassword)

	// Initialize
	store := sqlstore.New(db)
	server := NewServer(store)

	// Listen and Serve
	Logger.Infof("Server is listening on %s", ServerConfig.BindAddr)
	return http.ListenAndServe(ServerConfig.BindAddr, server)
}

func newDatabase(databaseURL string) (*sql.DB, error) {
	db, err := sql.Open("postgres", databaseURL)
	if err != nil {
		return nil, err
	}

	if err := db.Ping(); err != nil {
		return nil, err
	}

	return db, nil
}
