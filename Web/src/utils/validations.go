package utils

import validation "github.com/go-ozzo/ozzo-validation"

func RequiredIf(condition bool) validation.RuleFunc {
	return func(value interface{}) error {
		if condition {
			return validation.Validate(value, validation.Required)
		}

		return nil
	}
}
