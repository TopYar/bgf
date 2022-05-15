.PHONY: install 
install: ## Install
	go install cmd/server/main.go

.PHONY: build
build: ## Build
	go build cmd/server/main.go

.PHONY: run
run: ## Run
	./main -config-path=configs/server.toml

.PHONY: migrate
migrate: ## Migrate database
	migrate -database $(pq_url) -path migrations up

.PHONY: drop
drop: ## Drop everything inside database
	migrate -database $(pq_url) -path migrations drop

.PHONY: migratel
migratel: ## Migrate local database. Usage: 'make migratel dbname=bgf_dev'
	migrate -database "postgres://localhost/$(dbname)?sslmode=disable" -path migrations up

.PHONY: dropl
dropl: ## Drop everything inside local database. Usage: 'make dropl dbname=bgf_dev'
	migrate -database "postgres://localhost/$(dbname)?sslmode=disable" -path migrations drop

# Help ...
.PHONY: help
help:
	@awk 'BEGIN {FS = ":.*##"; printf "\nTargets:\n"} /^[a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)
	@echo "\n"

.DEFAULT_GOAL := help