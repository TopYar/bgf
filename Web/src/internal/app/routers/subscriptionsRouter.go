package routers

import (
	"bgf/internal/app/models"
	"bgf/internal/app/store/sqlstore"
	"bgf/utils/ctxkey"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
)

type subscriptionsRouter struct {
	router    *mux.Router
	store     *sqlstore.Store
	responder HttpController
}

func NewSubscriptionsRouter(router *mux.Router, store *sqlstore.Store, responder HttpController) BaseRouter {
	r := &subscriptionsRouter{
		router:    router,
		store:     store,
		responder: responder,
	}

	r.ConfigureRouter()

	return r
}

func (r *subscriptionsRouter) ConfigureRouter() {
	r.router.Use(r.responder.GetAuthorizeMw(true, "access"))
	r.router.HandleFunc("", r.HandleSubscribe()).Methods("POST")
	r.router.HandleFunc("", r.HandleUnsubscribe()).Methods("DELETE")
}

func (router *subscriptionsRouter) HandleSubscribe() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["id"]

		if len(query) == 0 {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdRequired)
			return
		}

		userId, err := strconv.Atoi(query[0])

		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
			return
		}

		currentUser := r.Context().Value(ctxkey.CtxUser).(*models.User)
		if err := router.store.SubscriptionsRepo().SubscribeToUser(userId, currentUser.Id); err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		router.responder.Respond(w, r, http.StatusOK, true)
	}
}

func (router *subscriptionsRouter) HandleUnsubscribe() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["id"]

		if len(query) == 0 {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdRequired)
			return
		}

		userId, err := strconv.Atoi(query[0])

		if err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
			return
		}

		currentUser := r.Context().Value(ctxkey.CtxUser).(*models.User)
		if err := router.store.SubscriptionsRepo().UnsubscribeFromUser(userId, currentUser.Id); err != nil {
			router.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		router.responder.Respond(w, r, http.StatusOK, true)
	}
}
