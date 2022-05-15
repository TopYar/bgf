package routers

import (
	"net/http"
)

type BaseRouter interface {
	ConfigureRouter()
}

type HttpController interface {
	// Error with error
	Error(writer http.ResponseWriter, request *http.Request, code int, err error)
	// Respond with data
	Respond(writer http.ResponseWriter, request *http.Request, code int, data interface{})
	// RespondHTML with data
	RespondHTML(writer http.ResponseWriter, request *http.Request, code int, html string)
	// GetAuthorizeMw Authorize request with special middleware
	GetAuthorizeMw(strict bool, jwtType string) func(next http.Handler) http.Handler
}
