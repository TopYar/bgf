package routers

import (
	"bgf/internal/app/store/sqlstore"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"net/http"
)

type eventRouter struct {
	router *mux.Router
	logger *logrus.Logger
	store  *sqlstore.Store
}

func NewEventRouter(router *mux.Router, logger *logrus.Logger, store *sqlstore.Store) BaseRouter {
	userrouter := &eventRouter{
		router: router,
		logger: logger,
		store:  store,
	}

	userrouter.ConfigureRouter()

	return userrouter
}

func (self *eventRouter) ConfigureRouter() {
	self.router.HandleFunc("/", self.handleEventsGet()).Methods("GET")
}

func (self *eventRouter) handleEventsGet() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusBadRequest)

		w.Header().Set("Content-Type", "application/json")
		//		w.Write([]byte(`{ "success": false,
		//"result": [{"id":1,"title":"Star wars","imageUrl":"http://ij.je/public/img/screenshots/Screenshot_151.png","date":"01.04.2022T12:00:00Z","tags":[ { "id": 0, "title": "mario" }, { "id": 1, "title": "hello" }, { "id": 2, "title": "it's me'" }],"limit":7,"visitorsCount":5, "likes": 3, "subscriptionStatus":"accepted","liked":true,"locationShort":"Москва, Россия","distance":5000,"creator":{"id":0,"nickname":"Лупа-сюпа","imageUrl":"https://ij.je/public/img/tg.png"}},{"id":2,"title":"Star trek","imageUrl":"http://ij.je/public/img/screenshots/Screenshot_149.png","date":"02.04.2022T12:00:00Z","tags":[ { "id": 3, "title": "let's" }, { "id": 4, "title": "play" }, { "id": 5, "title": "some games" }, { "id": 9, "title": "unknown" }, { "id": 10, "title": "tag" }],"limit":9,"visitorsCount":9,"likes": 4, "subscriptionStatus":"requested","liked":true,"locationShort":"Москва, Россия","distance":5100,"creator":{"id":1,"nickname":"Тиранозавр","imageUrl":"https://ij.je/public/img/wa.png"}},{"id":3,"title":"Бамблби","imageUrl":"http://ij.je/public/img/screenshots/Screenshot_150.png","date":"03.04.2022T12:00:00Z","tags":[ { "id": 6, "title": "wow" }, { "id": 6, "title": "wow" }, { "id": 7, "title": "qqq" }, { "id": 8, "title": "qqq" }],"limit":7,"visitorsCount":0,"likes": 2, "subscriptionStatus":"not_submitted","liked":false,"locationShort":"Москва, Россия","distance":5200,"creator":{"id":2,"nickname":"Rocky Balboa","imageUrl":"https://ij.je/public/img/vk.png"}}],
		//"error": { "msg": "wtf", "code": 10001 } }`))

		w.Write([]byte(`{ "success": false, 
"result": null,
"error": { "msg": "wtf", "code": 10001 } }`))
	}
}
