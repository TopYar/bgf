package responseDTO

import "bgf/internal/app/models/requestDTO"

type PageDTO struct {
	Page   requestDTO.PageDTO `json:"page"`
	Values interface{}        `json:"values"`
}
