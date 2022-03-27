package utils

import (
	. "bgf/configs"
	"github.com/golang-jwt/jwt"
)

func CreateJWT(claims *jwt.MapClaims) (string, error) {
	// Create the token
	token := jwt.New(jwt.SigningMethodHS256)
	// Set some claims
	token.Claims = claims
	// Sign and get the complete encoded token as a string
	tokenString, err := token.SignedString([]byte(ServerConfig.JWTSecretKey))
	return tokenString, err
}

func VerifyJWT(token string) (jwt.MapClaims, bool) {
	claims := &jwt.MapClaims{}
	tokenResult, err := jwt.ParseWithClaims(token, claims, func(token *jwt.Token) (interface{}, error) {
		return []byte(ServerConfig.JWTSecretKey), nil
	})

	if err != nil || !tokenResult.Valid {
		return nil, false
	}

	return *claims, true
}
