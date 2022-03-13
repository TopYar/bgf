package main

import (
	"bgf/utils"
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
)

type User struct {
	Id string `json:"id"`
}

func productsHandler(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id := vars["id"]
	user := User{Id: id}
	utils.RenderJSON(w, user)
}

func main() {

	router := mux.NewRouter()
	router.HandleFunc("/users/{id}", productsHandler)
	http.Handle("/", router)

	fmt.Println("Server is listening on 8007...")
	err := http.ListenAndServe(":8007", nil)

	if err != nil {
		return
	}
}
