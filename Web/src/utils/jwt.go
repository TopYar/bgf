package utils

import (
	. "bgf/configs"
	"github.com/golang-jwt/jwt"
)

type JwtClaims struct {
	jwt.StandardClaims
	IssuedAt  int64  `json:"iat"`
	ExpiresAt int64  `json:"exp"`
	Type      string `json:"type"`
	UserId    int    `json:"userId"`
	SessionId string `json:"sessionId,omitempty"`
}

func CreateJWT(claims *JwtClaims) (string, error) {
	// Create the token
	token := jwt.New(jwt.SigningMethodHS256)
	// Set some claims
	token.Claims = claims
	// Sign and get the complete encoded token as a string
	tokenString, err := token.SignedString([]byte(ServerConfig.JWTSecretKey))
	return tokenString, err
}

func VerifyJWT(token string) (*JwtClaims, bool) {
	claims := &JwtClaims{}
	tokenResult, err := jwt.ParseWithClaims(token, claims, func(token *jwt.Token) (interface{}, error) {
		return []byte(ServerConfig.JWTSecretKey), nil
	})

	if err != nil || !tokenResult.Valid {
		return nil, false
	}

	return claims, true
}
