package apiserver

import (
	"bgf/internal/app/models"
	"bgf/internal/app/routers"
	"bgf/internal/app/store"
	"bgf/internal/app/store/sqlstore"
	. "bgf/utils"
	"bgf/utils/ctxkey"
	"context"
	"encoding/base64"
	"errors"
	"net/http"
	"time"

	"github.com/google/uuid"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
)

var (
	errInvalidAuthHeader = errors.New("Invalid 'Authorization' header")
	errInvalidJwt        = errors.New("Invalid jwt")
	errWrongJwtType      = errors.New("Wrong jwt type")
	errFailQuerySession  = errors.New("Fail to get session")
	errFailQueryUser     = errors.New("Fail to get user")
	errFailFindSession   = errors.New("Fail to find session")
	errFailFindUser      = errors.New("Fail to find user")
	errOldSession        = errors.New("Session was deleted or expired")
	errWrongBase64       = errors.New("Wrong format")

	errUnexpectedError = errors.New("Unexpected behavior")
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
		self.store,
		self,
	)

	// Profile subrouter
	authSubrouter := v1_0.PathPrefix("/auth").Subrouter()
	//authSubrouter.Use(self.AuthorizeRequest)
	authRouter := routers.NewAuthRouter(
		authSubrouter,
		self.store,
		self,
	)

	subscriptionsRouter := routers.NewSubscriptionsRouter(
		v1_0.PathPrefix("/subscriptions").Subrouter(),
		self.store,
		self,
	)

	self.routers = append(self.routers, profileRouter, eventRouter, authRouter, subscriptionsRouter)

}

// Respond with error
func (self *server) Error(writer http.ResponseWriter, request *http.Request, code int, err error) {
	response := map[string]interface{}{
		"success": false,
		"result":  nil,
		"error":   err.Error(),
	}

	writer.Header().Add("Content-Type", "application/json")
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

	writer.Header().Add("Content-Type", "application/json")
	writer.WriteHeader(code)
	RenderJSON(writer, response)
}

// Respond with data
func (self *server) RespondHTML(writer http.ResponseWriter, request *http.Request, code int, html string) {
	writer.Header().Add("Content-Type", "text/html")
	writer.WriteHeader(code)
	writer.Write([]byte(html))
}

func (s *server) GetAuthorizeMw(strict bool, jwtType string) func(next http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(writer http.ResponseWriter, r *http.Request) {
			logger := Logger.WithFields(logrus.Fields{
				"request_id": r.Context().Value(ctxkey.CtxKeyRequestID),
			})
			rContext := r.Context()
			var session *models.Session = nil
			var user *models.User = nil

			authHeader := r.Header.Get("jwt")
			query := r.URL.Query()["j"]

			if len(authHeader) == 0 && len(query) == 0 {
				// If header was not presented or invalid, and if we should skip auth part
				if strict {
					logger.Warn("Invalid auth header")
					s.Error(writer, r, http.StatusUnauthorized, errInvalidAuthHeader)
					return
				}

				next.ServeHTTP(writer, r.WithContext(rContext))
				return
			}

			if len(query) > 0 {
				data, err := base64.StdEncoding.DecodeString(query[0])
				if err != nil {
					s.Error(writer, r, http.StatusInternalServerError, errWrongBase64)
					return
				}

				authHeader = string(data)
			}

			logger.Info("Checking jwt")

			token, isValid := VerifyJWT(authHeader)

			if !isValid {
				logger.Info("Invalid jwt")
				s.Error(writer, r, http.StatusUnauthorized, errInvalidJwt)
				return
			}

			logger.WithField("sessionId", token.SessionId)

			if token.SessionId != "" {
				var err error = nil

				session, err = s.store.SessionRepo().FindById(token.SessionId)

				if err != nil {
					if err != store.ErrRecordNotFound {
						logger.Errorf(`Can't query 'sessions' table`)
						s.Error(writer, r, http.StatusInternalServerError, errFailQuerySession)
						return
					}

					logger.Errorf(`Can't find session`)
					s.Error(writer, r, http.StatusInternalServerError, errFailFindSession)
					return
				}

				// Means DeletedAt != null, so session was deleted before
				if session.DeletedAt.Valid {
					s.Error(writer, r, http.StatusUnauthorized, errOldSession)
					return
				}
			}

			var userId = 0

			// Fetch user for context
			if token.Type == "confirmation" {
				userId = token.UserId
			} else {
				if session == nil {
					s.Error(writer, r, http.StatusUnauthorized, errInvalidJwt)
					return
				}
				userId = session.UserId
			}

			logger.WithField("userId", userId)

			user, err := s.store.UserRepo().FindById(userId)
			if err != nil {
				if err != store.ErrRecordNotFound {
					logger.Errorf(`Can't query 'users' table`)
					s.Error(writer, r, http.StatusInternalServerError, errFailQueryUser)
					return
				}

				logger.Errorf(`Can't find user`)
				s.Error(writer, r, http.StatusInternalServerError, errFailFindUser)
				return
			}

			// Check token type
			if token.Type != jwtType {
				s.Error(writer, r, http.StatusUnauthorized, errWrongJwtType)
				return
			}

			// Applies only for types 'refresh' and 'access'
			if session != nil {
				rContext = context.WithValue(rContext, ctxkey.CtxSession, session)
			}

			if user != nil {
				rContext = context.WithValue(rContext, ctxkey.CtxUser, user)
			} else {
				logger.Errorf(`User is somehow still equals to nil`)
				s.Error(writer, r, http.StatusInternalServerError, errUnexpectedError)
				return
			}

			next.ServeHTTP(writer, r.WithContext(rContext))
		})
	}
}

// Set requestID for logger
func (self *server) setRequestID(next http.Handler) http.Handler {
	return http.HandlerFunc(func(writer http.ResponseWriter, r *http.Request) {
		id := uuid.New().String()
		writer.Header().Set("x-Request-Id", id)
		requestIDContext := context.WithValue(r.Context(), ctxkey.CtxKeyRequestID, id)
		next.ServeHTTP(writer, r.WithContext(requestIDContext))
	})
}

// Log request start&complete
func (self *server) logRequest(next http.Handler) http.Handler {
	return http.HandlerFunc(func(writer http.ResponseWriter, request *http.Request) {
		logger := Logger.WithFields(logrus.Fields{
			"request_id": request.Context().Value(ctxkey.CtxKeyRequestID),
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
	})
}
