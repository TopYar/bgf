package routers

import (
	. "bgf/configs"
	"bgf/internal/app/models"
	"bgf/internal/app/store/sqlstore"
	. "bgf/utils"
	"bgf/utils/ctxkey"
	"encoding/base64"
	"encoding/json"
	"errors"
	"time"

	"github.com/gorilla/mux"

	//"errors"
	"net/http"
)

var (
	errFailedGenerateJWT    = errors.New("Cannot generate JWT token")
	errWrongEmailOrPassword = errors.New("Invalid email/password")
	errWrongEmail           = errors.New("Invalid email")
	errFailCreateSession    = errors.New("Fail to create new session")
	errFailRevokeSession    = errors.New("Fail to revoke session")
)

type authRouter struct {
	router    *mux.Router
	store     *sqlstore.Store
	responder HttpController
}

func NewAuthRouter(router *mux.Router, store *sqlstore.Store, responder HttpController) BaseRouter {
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

	// Authorized methods
	refreshRouter := self.router.PathPrefix("/refresh-token").Subrouter()
	refreshRouter.Use(self.responder.GetAuthorizeMw(true, "refresh"))
	refreshRouter.HandleFunc("", self.handleGetRefreshToken()).Methods("GET")

	signOutRouter := self.router.PathPrefix("/sign-out").Subrouter()
	signOutRouter.Use(self.responder.GetAuthorizeMw(true, "access"))
	signOutRouter.HandleFunc("", self.handleSignOut()).Methods("DELETE")

	revokeSessionsRouter := self.router.PathPrefix("/revoke-sessions").Subrouter()
	revokeSessionsRouter.Use(self.responder.GetAuthorizeMw(true, "access"))
	revokeSessionsRouter.HandleFunc("", self.handleRevokeSessions()).Methods("DELETE")

	// Recover methods
	self.router.HandleFunc("/recover", self.handleSendRecoverLink()).Methods("POST")
	recoverSessionsRouter := self.router.PathPrefix("/recover").Subrouter()
	recoverSessionsRouter.Use(self.responder.GetAuthorizeMw(true, "recover"))
	recoverSessionsRouter.HandleFunc("", self.handleRecoverPassword()).Methods("GET")
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

		self.responder.Respond(w, r, http.StatusAccepted, map[string]string{
			"accessToken":  accessToken,
			"refreshToken": refreshToken,
		})
	}
}

func (self *authRouter) handleSendRecoverLink() http.HandlerFunc {
	type request struct {
		Email string `json:"email"`
	}

	return func(w http.ResponseWriter, r *http.Request) {
		request := &request{}
		if err := json.NewDecoder(r.Body).Decode(request); err != nil {
			self.responder.Error(w, r, http.StatusBadRequest, err)
			return
		}

		user, err := self.store.UserRepo().FindByEmail(request.Email)
		if err != nil {
			self.responder.Error(w, r, http.StatusUnauthorized, errWrongEmail)
			return
		}

		session, err := self.store.SessionRepo().New(user.Id)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, errFailCreateSession)
			return
		}

		claims := &JwtClaims{
			SessionId: session.Id,
			UserId:    user.Id,
			Type:      "recover",
		}

		token, err := createJwt(claims, ServerConfig.RecoverPasswordExpiration)

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}
		tokenBase64 := base64.StdEncoding.EncodeToString([]byte(token))
		link := ServerConfig.BaseUrl + ServerConfig.VersionApi + "/auth/recover?j=" + tokenBase64

		body, err := RenderTemplate("recover", map[string]interface{}{"link": link})

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		if err := SendMail(user.Email, "Recover password", body); err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		self.responder.Respond(w, r, http.StatusAccepted, true)
	}
}

func (self *authRouter) handleRecoverPassword() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var user = r.Context().Value(ctxkey.CtxUser).(*models.User)
		var session = r.Context().Value(ctxkey.CtxSession).(*models.Session)

		if err := self.store.SessionRepo().RevokeAllSessions(session.UserId); err != nil {
			self.responder.Respond(w, r, http.StatusInternalServerError, errFailRevokeSession)
		}

		user.Password, _ = GenerateRandomString(8)

		if err := self.store.UserRepo().UpdatePassword(user); err != nil {
			self.responder.Respond(w, r, http.StatusInternalServerError, err)
		}

		body, err := RenderTemplate("newpassword", map[string]interface{}{"password": user.Password})

		if err != nil {
			self.responder.Error(w, r, http.StatusInternalServerError, err)
			return
		}

		self.responder.RespondHTML(w, r, http.StatusOK, body)
	}
}

// Need implementation
func (self *authRouter) handleGetRefreshToken() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var session = r.Context().Value(ctxkey.CtxSession).(*models.Session)
		claims := &JwtClaims{
			SessionId: session.Id,
			UserId:    session.UserId,
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

func (self *authRouter) handleSignOut() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var session = r.Context().Value(ctxkey.CtxSession).(*models.Session)

		if err := self.store.SessionRepo().RevokeSession(session.Id); err != nil {
			self.responder.Respond(w, r, http.StatusInternalServerError, errFailRevokeSession)
		}

		self.responder.Respond(w, r, http.StatusOK, true)
	}
}

func (self *authRouter) handleRevokeSessions() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var session = r.Context().Value(ctxkey.CtxSession).(*models.Session)

		if err := self.store.SessionRepo().RevokeAllSessionsExceptCurrent(session.UserId, session.Id); err != nil {
			self.responder.Respond(w, r, http.StatusInternalServerError, errFailRevokeSession)
		}

		self.responder.Respond(w, r, http.StatusOK, true)
	}
}

func createJwt(claimsPtr *JwtClaims, expire time.Duration) (string, error) {
	claims := *claimsPtr
	claims.IssuedAt = time.Now().Unix()
	claims.ExpiresAt = time.Now().Add(expire).Unix()

	token, err := CreateJWT(&claims)
	if err != nil {
		return "", errFailedGenerateJWT
	}

	return token, nil
}

func createJwtPair(claimsPtr *JwtClaims) (string, string, error) {
	claims := *claimsPtr
	claims.IssuedAt = time.Now().Unix()

	accessClaims, refreshClaims := claims, claims

	accessClaims.ExpiresAt = time.Now().Add(ServerConfig.AccessTokenExpiration).Unix()
	refreshClaims.ExpiresAt = time.Now().Add(ServerConfig.RefreshTokenExpiration).Unix()

	accessClaims.Type = "access"
	refreshClaims.Type = "refresh"

	accessToken, errAccess := CreateJWT(&accessClaims)
	refreshToken, errRefresh := CreateJWT(&refreshClaims)

	if errAccess != nil || errRefresh != nil {
		return "", "", errFailedGenerateJWT
	}

	return accessToken, refreshToken, nil
}
