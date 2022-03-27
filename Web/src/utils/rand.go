package utils

import (
	"crypto/rand"
	"math/big"
)

const letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
const numbers = "0123456789"

func generateRandomWithDictionary(n int, letters string) (string, error) {
	ret := make([]byte, n)
	for i := 0; i < n; i++ {
		num, err := rand.Int(rand.Reader, big.NewInt(int64(len(letters))))
		if err != nil {
			return "", err
		}
		ret[i] = letters[num.Int64()]
	}

	return string(ret), nil
}

func GenerateRandomString(n int) (string, error) {
	return generateRandomWithDictionary(n, letters)
}

func GenerateRandomNumberSequence(n int) (string, error) {
	return generateRandomWithDictionary(n, numbers)
}
