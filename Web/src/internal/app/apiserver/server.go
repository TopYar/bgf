package apiserver

import (
	"bgf/internal/app/routers"
	"bgf/internal/app/store/sqlstore"
	. "bgf/utils"
	"context"
	"net/http"
	"time"

	"github.com/google/uuid"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
)

const (
	ctxKeyRequestID = "ctxKeyRequestID"
)

type server struct {
	router  *mux.Router
	logger  *logrus.Logger
	store   *sqlstore.Store
	routers []routers.BaseRouter
}

func NewServer(store *sqlstore.Store) *server {
	server := &server{
		router:  mux.NewRouter(),
		store:   store,
		routers: []routers.BaseRouter{},
	}

	server.configureRouter()
	return server
}

// http.Handler interface
func (self *server) ServeHTTP(writer http.ResponseWriter, request *http.Request) {
	self.router.ServeHTTP(writer, request)
}

func (self *server) configureRouter() {

	// Logger middleware
	self.router.Use(self.setRequestID)
	self.router.Use(self.logRequest)

	// Version subrouting
	v1_0 := self.router.PathPrefix("/v1.0").Subrouter()

	// Profile subrouter
	profileRouter := routers.NewUserRouter(
		v1_0.PathPrefix("/profile").Subrouter(),
		self.store,
		self,
	)

	// Event subrouter
	eventRouter := routers.NewEventRouter(
		v1_0.PathPrefix("/events").Subrouter(),
		self.logger,
		self.store,
	)

	// Profile subrouter
	authRouter := routers.NewAuthRouter(
		v1_0.PathPrefix("/auth").Subrouter(),
		self.store,
		self,
	)

	self.routers = append(self.routers, profileRouter, eventRouter, authRouter)

}

// Respond with error
func (self *server) Error(writer http.ResponseWriter, request *http.Request, code int, err error) {
	response := map[string]interface{}{
		"success": false,
		"result":  nil,
		"error":   err.Error(),
	}

	writer.WriteHeader(code)
	RenderJSON(writer, response)
}

// Respond with data
func (self *server) Respond(writer http.ResponseWriter, request *http.Request, code int, data interface{}) {
	response := map[string]interface{}{
		"success": true,
		"result":  data,
		"error":   nil,
	}

	writer.WriteHeader(code)
	RenderJSON(writer, response)
}

// Set requestID for logger
func (self *server) setRequestID(next http.Handler) http.Handler {
	return http.HandlerFunc(func(writer http.ResponseWriter, r *http.Request) {
		id := uuid.New().String()
		writer.Header().Set("x-Request-Id", id)
		requestIDContext := context.WithValue(r.Context(), ctxKeyRequestID, id)
		next.ServeHTTP(writer, r.WithContext(requestIDContext))
	})
}

// Log request start&complete
func (self *server) logRequest(next http.Handler) http.Handler {
	return http.HandlerFunc(func(writer http.ResponseWriter, request *http.Request) {
		logger := Logger.WithFields(logrus.Fields{
			"request_id": request.Context().Value(ctxKeyRequestID),
		})
		logger.Infof(
			"Started %s %s from %s",
			request.Method,
			request.RequestURI,
			request.RemoteAddr,
		)

		startTime := time.Now()
		rwriter := NewMwResponseWriter(writer)
		next.ServeHTTP(rwriter, request)

		logger.Infof(
			"Completed with %d %s in %v",
			rwriter.statusCode,
			http.StatusText(*rwriter.statusCode),
			time.Now().Sub(startTime),
		)

		logger.Infof(
			"Response data:\n%s",
			rwriter.String(),
		)
	})
}
