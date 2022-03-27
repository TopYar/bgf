package routers

import (
	"net/http"
)

type BaseRouter interface {
	ConfigureRouter()
}

type HttpResponder interface {
	// Respond with error
	Error(writer http.ResponseWriter, request *http.Request, code int, err error)
	// Respond with data
	Respond(writer http.ResponseWriter, request *http.Request, code int, data interface{})
}
