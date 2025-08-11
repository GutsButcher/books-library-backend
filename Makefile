.PHONY: help build run stop test clean logs

help:
	@echo "Available commands:"
	@echo "  make build   - Build the Docker images"
	@echo "  make run     - Run the application with docker-compose"
	@echo "  make stop    - Stop the application"
	@echo "  make test    - Run tests in Docker"
	@echo "  make clean   - Clean up containers and volumes"
	@echo "  make logs    - Show application logs"

build:
	docker-compose build

run:
	docker-compose up -d

stop:
	docker-compose down

test:
	docker-compose -f docker-compose.test.yml up --build --abort-on-container-exit
	docker-compose -f docker-compose.test.yml down

clean:
	docker-compose down -v
	docker-compose -f docker-compose.test.yml down -v

logs:
	docker-compose logs -f app