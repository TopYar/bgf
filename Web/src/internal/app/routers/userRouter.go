package routers

import (
	"bgf/internal/app/model"
	"bgf/internal/app/store"
	"bgf/internal/app/store/sqlstore"
	. "bgf/utils"
	"encoding/json"
	"errors"
	"github.com/golang-jwt/jwt"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
)

var (
	errExpectedProfileID    = errors.New("Expected profile id")
	errExpectedProfileEmail = errors.New("Expected profile email")
)

type userRouter struct {
	router    *mux.Router
	store     *sqlstore.Store
	responder HttpResponder
}

func NewUserRouter(router *mux.Router, store *sqlstore.Store, responder HttpResponder) BaseRouter {
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
	self.router.HandleFunc("/confirm", self.handleUserConfirm()).Methods("PUT")
	self.router.HandleFunc("/find", self.handleUserFindByEmail()).Methods("GET")
	self.router.HandleFunc("/{id}", self.handleUserFindById()).Methods("GET")
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

		user := &model.User{
			Email:    request.Email,
			Password: request.Password,
		}

		if err := self.store.UserRepo().Create(user); err != nil {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, err)
			return
		}

		user.Sanitize()

		codeSequence, err := GenerateRandomNumberSequence(4)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		code := model.CreateConfirmationCode(user.Id, codeSequence)

		if err := self.store.CofirmationCodeRepo().Create(code); err != nil {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, err)
			return
		}

		//SendMail([]string{user.Email}, "Confirmation code", code.Code)

		claims := &jwt.MapClaims{
			"data": map[string]interface{}{
				"sessionId": "1",
				"userId":    user.Id,
			},
		}

		jwt, err := CreateJWT(claims)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		self.responder.Respond(w, r, http.StatusCreated, jwt)
	}
}

func (self *userRouter) handleUserConfirm() http.HandlerFunc {
	type request struct {
		Code string `json:"code"`
	}

	return func(w http.ResponseWriter, r *http.Request) {
		token, isValid := VerifyJWT(r.Header.Get("jwt"))

		if !isValid {
			self.responder.Error(w, r, http.StatusUnauthorized, errors.New("not valid jwt"))
			return
		}

		request := &request{}
		if err := json.NewDecoder(r.Body).Decode(request); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		data := token["data"].(map[string]interface{})
		userId := data["userId"].(string)

		code, err := self.store.CofirmationCodeRepo().FindByUserId(userId)

		if err != nil {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, err)
			return
		}

		if code.Code != request.Code {
			self.responder.Error(w, r, http.StatusUnauthorized, errors.New("code is not valid"))
			return
		}

		if _, err := self.store.CofirmationCodeRepo().DeleteAllUserCodes(userId); err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, errors.New("can't delete codes"))
			return
		}

		claims := &jwt.MapClaims{
			"data": map[string]interface{}{
				// TODO: припилить сессии
				"sessionId": "1",
				"userId":    userId,
			},
		}

		accessToken, refreshToken, err := createJwtPair(claims)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		self.responder.Respond(w, r, http.StatusAccepted, map[string]string{
			"accessToken":  accessToken,
			"refreshToken": refreshToken,
		})
	}
}

func (self *userRouter) handleUserFindById() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		strId, ok := mux.Vars(r)["id"]
		if !ok {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, errExpectedProfileID)
			return
		}

		id, err := strconv.Atoi(strId)
		if err != nil {
			self.responder.Error(w, r, http.StatusUnprocessableEntity, err)
			return
		}

		user, err := self.store.UserRepo().FindById(id)
		if err != nil {
			if err == store.ErrRecordNotFound {
				self.responder.Respond(w, r, http.StatusNoContent, nil)
			} else {
				self.responder.Error(w, r, http.StatusInternalServerError, err)
			}
			return
		}

		w.WriteHeader(http.StatusOK)
		RenderJSON(w, user)
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
