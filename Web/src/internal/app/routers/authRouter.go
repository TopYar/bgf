package routers

import (
	. "bgf/configs"
	"bgf/internal/app/store"
	"bgf/internal/app/store/sqlstore"
	"bgf/utils"
	"encoding/json"
	"errors"
	"github.com/golang-jwt/jwt"
	"github.com/gorilla/mux"
	"time"

	//"errors"
	"net/http"
	"strconv"
)

var (
	errFailedGenerateJWT    = errors.New("Cannot generate JWT token")
	errWrongEmailOrPassword = errors.New("Invalid email/password")
)

type authRouter struct {
	router    *mux.Router
	store     *sqlstore.Store
	responder HttpResponder
}

func NewAuthRouter(router *mux.Router, store *sqlstore.Store, responder HttpResponder) BaseRouter {
	authrouter := &authRouter{
		router:    router,
		store:     store,
		responder: responder,
	}

	authrouter.ConfigureRouter()

	return authrouter
}

func (self *authRouter) ConfigureRouter() {
	self.router.HandleFunc("/sign-in", self.handleSignIn()).Methods("POST")
	self.router.HandleFunc("/refresh-token", self.handleUserFindByEmail()).Methods("GET")
	self.router.HandleFunc("/sign-out", self.handleUserFindById()).Methods("GET")
}

func (self *authRouter) handleSignIn() http.HandlerFunc {
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

		user, err := self.store.UserRepo().FindByEmail(request.Email)
		if err != nil || !user.PasswordEqualTo(request.Password) {
			self.responder.Error(w, r, http.StatusUnauthorized, errWrongEmailOrPassword)
			return
		}

		claims := &jwt.MapClaims{
			"data": map[string]interface{}{
				// TODO: припилить сессии
				"sessionId": "1",
				"userId":    user.Id,
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

func createJwtPair(claimsPtr *jwt.MapClaims) (string, string, error) {
	claims := *claimsPtr
	claims["iat"] = time.Now().Unix()

	accessClaims, refreshClaims := claims, claims

	accessClaims["exp"] = time.Now().Add(ServerConfig.AccessTokenExpiration).Unix()
	refreshClaims["exp"] = time.Now().Add(ServerConfig.RefreshTokenExpiration).Unix()

	accessToken, errAccess := utils.CreateJWT(&accessClaims)
	refreshToken, errRefresh := utils.CreateJWT(&refreshClaims)

	if errAccess != nil || errRefresh != nil {
		return "", "", errFailedGenerateJWT
	}

	return accessToken, refreshToken, nil
}

func (self *authRouter) handleUserFindById() http.HandlerFunc {
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
		utils.RenderJSON(w, user)
	}
}

func (self *authRouter) handleUserFindByEmail() http.HandlerFunc {
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
