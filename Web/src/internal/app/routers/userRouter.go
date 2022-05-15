package routers

import (
	. "bgf/configs"
	"bgf/internal/app/models"
	"bgf/internal/app/store"
	"bgf/internal/app/store/sqlstore"
	. "bgf/utils"
	"bgf/utils/ctxkey"
	"encoding/json"
	"errors"
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
)

var (
	errExpectedProfileID    = errors.New("Expected profile id")
	errExpectedProfileEmail = errors.New("Expected profile email")
)

type userRouter struct {
	router    *mux.Router
	store     *sqlstore.Store
	responder HttpController
}

func NewUserRouter(router *mux.Router, store *sqlstore.Store, responder HttpController) BaseRouter {
	userrouter := &userRouter{
		router:    router,
		store:     store,
		responder: responder,
	}

	userrouter.ConfigureRouter()

	return userrouter
}

func (self *userRouter) ConfigureRouter() {
	self.router.HandleFunc("", self.handleUserCreate()).Methods("POST")

	confirmRouter := self.router.PathPrefix("/confirm").Subrouter()
	confirmRouter.Use(self.responder.GetAuthorizeMw(true, "confirmation"))
	confirmRouter.HandleFunc("", self.handleUserConfirm()).Methods("PUT")

	getRouter := self.router.PathPrefix("").Subrouter()
	getRouter.Use(self.responder.GetAuthorizeMw(true, "access"))
	getRouter.HandleFunc("", self.handleGetUser()).Methods("GET")
	getRouter.HandleFunc("/find", self.handleUserFindByEmail()).Methods("GET")
}

func (self *userRouter) handleUserCreate() http.HandlerFunc {
	type request struct {
		Email    string `json:"email"`
		Password string `json:"password"`
	}

	return func(w http.ResponseWriter, r *http.Request) {
		request := &request{}
		if err := json.NewDecoder(r.Body).Decode(request); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		user := &models.User{
			Email:    request.Email,
			Password: request.Password,
		}

		if err := self.store.UserRepo().Create(user); err != nil {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, err)
			return
		}
		_ = self.store.UserRepo().SetDefaultNickname(user)

		user.Sanitize()

		codeSequence, err := GenerateRandomNumberSequence(4)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		code := models.CreateConfirmationCode(user.Id, codeSequence)

		if err := self.store.CofirmationCodeRepo().Create(code); err != nil {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, err)
			return
		}

		body, err := RenderTemplate("mail", map[string]interface{}{"code": code.Code})

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		if err := SendMail(user.Email, "Confirmation code", body); err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		claims := &JwtClaims{
			UserId: user.Id,
			Type:   "confirmation",
		}

		jwtToken, err := createJwt(claims, ServerConfig.ConfirmationCodeExpiration)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		self.responder.Respond(w, r, http.StatusCreated, map[string]string{
			"token": jwtToken,
		})
	}
}

func (self *userRouter) handleUserConfirm() http.HandlerFunc {
	type request struct {
		Code string `json:"code"`
	}

	return func(w http.ResponseWriter, r *http.Request) {
		request := &request{}
		if err := json.NewDecoder(r.Body).Decode(request); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}
		var user = r.Context().Value(ctxkey.CtxUser).(*models.User)

		code, err := self.store.CofirmationCodeRepo().FindByUserId(user.Id)

		if err != nil {
			self.responder.Error(w, r, http.StatusNotFound, err)
			return
		}

		if code.Code != request.Code {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, errors.New("code is not valid"))
			return
		}

		// Create session
		session, err := self.store.SessionRepo().New(user.Id)
		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, errFailCreateSession)
			return
		}

		claims := &JwtClaims{
			SessionId: session.Id,
			UserId:    user.Id,
		}

		accessToken, refreshToken, err := createJwtPair(claims)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		// Success => delete all codes, but in case of error - just log it
		if err := self.store.CofirmationCodeRepo().DeleteAllUserCodes(user.Id); err != nil {
			Logger.Error("Can't delete all codes")
		}

		self.responder.Respond(w, r, http.StatusAccepted, map[string]string{
			"accessToken":  accessToken,
			"refreshToken": refreshToken,
		})
	}
}

func (self *userRouter) handleUserFindById() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {

	}
}

func (self *userRouter) handleGetUser() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		query := r.URL.Query()["id"]
		currentUser := r.Context().Value(ctxkey.CtxUser).(*models.User)

		var userId int
		var err error

		if len(query) == 0 {
			userId = currentUser.Id
		} else {
			userId, err = strconv.Atoi(query[0])
			if err != nil {
				self.responder.Error(w, r, http.StatusBadRequest, errEventIdInvalid)
				return
			}
		}

		user, err := self.store.UserRepo().FindByIdWithSubscriptions(currentUser.Id, userId)
		if err != nil {
			if err == store.ErrRecordNotFound {
				self.responder.Respond(w, r, http.StatusNoContent, nil)
			} else {
				self.responder.Error(w, r, http.StatusInternalServerError, err)
			}
			return
		}

		self.responder.Respond(w, r, http.StatusOK, user)
	}
}

func (self *userRouter) handleUserFindByEmail() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		email := r.FormValue("email")
		if len(email) == 0 {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, errExpectedProfileEmail)
			return
		}

		user, err := self.store.UserRepo().FindByEmail(email)
		if err != nil {
			if err == store.ErrRecordNotFound {
				self.responder.Respond(w, r, http.StatusNoContent, nil)
			} else {
				self.responder.Error(w, r, http.StatusInternalServerError, err)
			}
			return
		}

		self.responder.Respond(w, r, http.StatusOK, user)
	}
}
