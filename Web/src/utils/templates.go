package utils

import (
	"bytes"
	"fmt"
	"html/template"
	"io/ioutil"
)

func RenderTemplate(name string, values map[string]interface{}) (string, error) {
	content, err := ioutil.ReadFile(fmt.Sprintf("static/templates/%s.tmpl", name))
	if err != nil {
		return "", err
	}

	t, err := template.New(name).Parse(string(content))
	if err != nil {
		return "", err
	}

	var buff bytes.Buffer
	err = t.Execute(&buff, values)
	if err != nil {
		return "", err
	}

	return buff.String(), nil
}
