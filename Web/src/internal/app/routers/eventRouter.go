package routers

import (
	"bgf/internal/app/models"
	"bgf/internal/app/models/requestDTO"
	responseDTO "bgf/internal/app/models/responsesDTO"
	"bgf/internal/app/store"
	"bgf/internal/app/store/sqlstore"
	"bgf/utils/ctxkey"
	"encoding/json"
	"errors"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/gorilla/schema"
)

var (
	errEventIdRequired = errors.New("'eventId' is required query parameter")
	errEventIdInvalid  = errors.New("'eventId' is invalid format. Int expected")
)

type eventRouter struct {
	router    *mux.Router
	store     *sqlstore.Store
	responder HttpController
}

func NewEventRouter(router *mux.Router, store *sqlstore.Store, responder HttpController) BaseRouter {
	r := &eventRouter{
		router:    router,
		store:     store,
		responder: responder,
	}

	r.ConfigureRouter()

	return r
}

func (r *eventRouter) ConfigureRouter() {
	r.router.Use(r.responder.GetAuthorizeMw(true, "access"))
	r.router.HandleFunc("", r.handleEventsGet()).Methods("GET")
	r.router.HandleFunc("/visitors", r.handleEventsGetVisitors()).Methods("GET")
	r.router.HandleFunc("", r.handleEventCreate()).Methods("POST")
	r.router.HandleFunc("/likes", r.handleEventAddLike()).Methods("POST")
	r.router.HandleFunc("/likes", r.handleEventRemoveLike()).Methods("DELETE")
	r.router.HandleFunc("/participate", r.handleMakeParticipation()).Methods("POST")
	r.router.HandleFunc("/participate", r.handleRemoveParticipation()).Methods("DELETE")

	mr := r.router.PathPrefix("/my").Subrouter()
	mr.HandleFunc("/created", r.handleEventsGetCreated()).Methods("GET")
	mr.HandleFunc("/liked", r.handleEventsGetLiked()).Methods("GET")
	mr.HandleFunc("/participated", r.handleEventsGetParticipated()).Methods("GET")
}

func (router *eventRouter) handleEventsGetVisitors() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["eventId"]

		if len(query) == 0 {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdRequired)
			return
		}

		eventId, err := strconv.Atoi(query[0])

		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
			return
		}

		reqDTO := &requestDTO.EventIdDTO{EventId: eventId}

		users, err := router.store.EventsRepo().GetVisitors(reqDTO.EventId)
		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		router.responder.Respond(w, r, http.StatusOK, users)
	}
}

func (self *eventRouter) handleEventsGet() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["eventId"]
		user := r.Context().Value(ctxkey.CtxUser).(*models.User)

		if len(query) != 0 {
			eventId, err := strconv.Atoi(query[0])
			if err != nil {
				self.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
				return
			}

			reqDTO := &requestDTO.EventIdDTO{EventId: eventId}

			event, err := self.store.EventsRepo().GetOne(user.Id, reqDTO.EventId)
			if err != nil {
				self.responder.Error(w, r, http.StatusBadRequest, err)
				return
			}

			self.responder.Respond(w, r, http.StatusOK, event)
			return
		}

		page := &requestDTO.PageDTO{
			Limit:  20,
			Offset: 0,
		}
		if err := schema.NewDecoder().Decode(page, r.URL.Query()); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		events, err := self.store.EventsRepo().Get(user.Id, page.Offset, page.Limit)
		if err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		response := &responseDTO.PageDTO{}
		response.Page = *page
		response.Values = events

		self.responder.Respond(w, r, http.StatusOK, response.Values)
	}
}

func (router *eventRouter) handleEventCreate() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		requestDTO := &requestDTO.CreateEventDTO{}
		if err := json.NewDecoder(r.Body).Decode(requestDTO); err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}
		if err := requestDTO.Validate(); err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		if err := router.store.EventsRepo().Create(user.Id, requestDTO); err != nil {
			router.responder.Error(w, r, http.StatusUnprocessableEntity, err)
			return
		}

		router.responder.Respond(w, r, http.StatusCreated, requestDTO.Id)
	}
}

func (self *eventRouter) handleEventsGetCreated() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {

		page := &requestDTO.PageDTO{
			Limit: 20,
		}
		if err := schema.NewDecoder().Decode(page, r.URL.Query()); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		events, err := self.store.EventsRepo().GetCreated(user.Id, page.Offset, page.Limit)
		if err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		response := &responseDTO.PageDTO{}
		response.Page = *page
		response.Values = events

		self.responder.Respond(w, r, http.StatusOK, response.Values)
	}
}

func (self *eventRouter) handleEventsGetLiked() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {

		page := &requestDTO.PageDTO{}
		if err := schema.NewDecoder().Decode(page, r.URL.Query()); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		events, err := self.store.EventsRepo().GetLiked(user.Id, page.Offset, page.Limit)
		if err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		response := &responseDTO.PageDTO{}
		response.Page = *page
		response.Values = events

		self.responder.Respond(w, r, http.StatusOK, response)
	}
}

func (self *eventRouter) handleEventsGetParticipated() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {

		page := &requestDTO.PageDTO{}
		if err := schema.NewDecoder().Decode(page, r.URL.Query()); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		events, err := self.store.EventsRepo().GetParticipated(user.Id, page.Offset, page.Limit)
		if err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		response := &responseDTO.PageDTO{}
		response.Page = *page
		response.Values = events

		self.responder.Respond(w, r, http.StatusOK, response)
	}
}

func (router *eventRouter) handleEventAddLike() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["eventId"]

		if len(query) == 0 {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdRequired)
			return
		}

		eventId, err := strconv.Atoi(query[0])

		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
			return
		}

		reqDTO := &requestDTO.EventIdDTO{EventId: eventId}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		if err := router.store.EventsLikesRepo().LikeEvent(reqDTO.EventId, user.Id); err != nil {
			router.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		router.responder.Respond(w, r, http.StatusOK, nil)
	}
}

func (router *eventRouter) handleEventRemoveLike() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["eventId"]

		if len(query) == 0 {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdRequired)
			return
		}

		eventId, err := strconv.Atoi(query[0])

		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
			return
		}

		reqDTO := &requestDTO.EventIdDTO{EventId: eventId}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		if err := router.store.EventsLikesRepo().RemoveLikeEvent(reqDTO.EventId, user.Id); err != nil {
			if err == store.ErrRecordNotFound {
				router.responder.Error(w, r, http.StatusBadRequest, err)
			} else {
				router.responder.Error(w, r, http.StatusInternalServerError, err)
			}
			return
		}

		router.responder.Respond(w, r, http.StatusOK, nil)
	}
}

func (router *eventRouter) handleMakeParticipation() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["eventId"]

		if len(query) == 0 {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdRequired)
			return
		}

		eventId, err := strconv.Atoi(query[0])

		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
			return
		}

		reqDTO := &requestDTO.EventIdDTO{EventId: eventId}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		if err := router.store.EventsParticipationRepo().MakeParticipationInEvent(reqDTO.EventId, user.Id); err != nil {
			router.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		router.responder.Respond(w, r, http.StatusOK, "accepted")
	}
}

func (router *eventRouter) handleRemoveParticipation() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["eventId"]

		if len(query) == 0 {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdRequired)
			return
		}

		eventId, err := strconv.Atoi(query[0])

		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
			return
		}

		reqDTO := &requestDTO.EventIdDTO{EventId: eventId}

		user := r.Context().Value(ctxkey.CtxUser).(*models.User)
		if err := router.store.EventsParticipationRepo().RemoveParticipationInEvent(reqDTO.EventId, user.Id); err != nil {
			router.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		router.responder.Respond(w, r, http.StatusOK, nil)
	}
}
