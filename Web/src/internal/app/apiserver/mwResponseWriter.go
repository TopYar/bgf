package apiserver

import (
	"bytes"
	"fmt"
	"net/http"
)

// mwResponseWriter struct is used to log the response
type mwResponseWriter struct {
	w          *http.ResponseWriter
	body       *bytes.Buffer
	statusCode *int
}

// NewMwResponseWriter static function creates a wrapper for the http.ResponseWriter
func NewMwResponseWriter(w http.ResponseWriter) mwResponseWriter {
	var buf bytes.Buffer
	var statusCode int = 200
	return mwResponseWriter{
		w:          &w,
		body:       &buf,
		statusCode: &statusCode,
	}
}

func (mrw mwResponseWriter) Write(buf []byte) (int, error) {
	mrw.body.Write(buf)
	return (*mrw.w).Write(buf)
}

// Header function overwrites the http.ResponseWriter Header() function
func (mrw mwResponseWriter) Header() http.Header {
	return (*mrw.w).Header()

}

// WriteHeader function overwrites the http.ResponseWriter WriteHeader() function
func (mrw mwResponseWriter) WriteHeader(statusCode int) {
	*mrw.statusCode = statusCode
	(*mrw.w).WriteHeader(statusCode)
}

func (mrw mwResponseWriter) String() string {
	var buf bytes.Buffer

	buf.WriteString("Headers: \n")
	for k, v := range (*mrw.w).Header() {
		buf.WriteString(fmt.Sprintf("%s: %v\n", k, v))
	}

	buf.WriteString(fmt.Sprintf("\nStatus Code: %d\n", *(mrw.statusCode)))

	buf.WriteString("Body:\n")
	buf.WriteString(mrw.body.String())
	return buf.String()
}
