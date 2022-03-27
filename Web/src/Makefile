# Install ...
.PHONY: install
install:
	go install cmd/server/main.go

# Build ...
.PHONY: build
build:
	go build cmd/server/main.go

# Run ...
.PHONY: run
run:
	./main -config-path=configs/server.toml

# Migrate database ...
.PHONY: migrate
migrate:
	migrate -database $(pq_url) -path migrations up

# Help ...
.PHONY: help
help:
	@echo "Available targets:"
	@echo "- install\n- build\n- run\n- help"

.DEFAULT_GOAL := help