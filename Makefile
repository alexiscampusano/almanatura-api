.PHONY: help build rebuild up up-logs up-dev up-tools down down-volumes restart \
        logs logs-api logs-db status shell db-shell clean test verify format format-check \
	coverage db-backup db-restore seed-demo db-reset db-reseed wait-db \
        prod-up prod-up-logs prod-down prod-restart prod-logs prod-status prod-build

DOCKER_COMPOSE      = docker compose
DOCKER_COMPOSE_DEV  = docker compose -f docker-compose.yml -f docker-compose.dev.yml
DOCKER_COMPOSE_PROD = docker compose -f docker-compose.yml -f docker-compose.prod.yml

help:
	@echo "AlmaNatura API - available commands:"
	@echo "  make build         - Build the base Docker images"
	@echo "  make rebuild       - Rebuild without cache"
	@echo "  make up            - Start the base stack in detached mode"
	@echo "  make up-logs       - Start the base stack attached"
	@echo "  make up-dev        - Start the development stack (hot reload + debug port 5005)"
	@echo "  make up-tools      - Start phpMyAdmin via the tools profile"
	@echo "  make down          - Stop the base stack"
	@echo "  make down-volumes  - Stop and remove volumes (resets the database)"
	@echo "  make restart       - Restart base containers"
	@echo "  make logs          - Tail all base logs"
	@echo "  make logs-api      - Tail API logs"
	@echo "  make logs-db       - Tail DB logs"
	@echo "  make status        - Show base container status"
	@echo "  make shell         - Shell into the API container"
	@echo "  make db-shell      - Open a MySQL shell in the DB container"
	@echo "  make test          - Run unit tests with the Maven wrapper"
	@echo "  make verify        - Full build: tests + JaCoCo + Spotless + ArchUnit"
	@echo "  make coverage      - Build and point to the JaCoCo HTML report"
	@echo "  make format        - Auto-format Java sources with Spotless"
	@echo "  make format-check  - Verify formatting without modifying files"
	@echo "  make db-backup     - Dump the database to ./backups"
	@echo "  make db-restore    - Restore from FILE=./backups/your.sql"
	@echo "  make seed-demo     - Load demo actors + projects (idempotent)"
	@echo "  make db-reset      - Recreate the stack with an empty database"
	@echo "  make db-reseed     - Reset the database and load demo data"
	@echo "  make clean         - Remove containers, images and volumes"
	@echo ""
	@echo "Development: docker-compose.dev.yml"
	@echo "  make up-dev        - Build and run the dev image with source mount"
	@echo ""
	@echo "Production: docker-compose.prod.yml"
	@echo "  make prod-build    - Build images with production overrides"
	@echo "  make prod-up       - Start the production stack in background"
	@echo "  make prod-up-logs  - Start the production stack attached"
	@echo "  make prod-down     - Stop the production stack"
	@echo "  make prod-restart  - Restart the production stack"
	@echo "  make prod-logs     - Tail production logs"
	@echo "  make prod-status   - Show production container status"

build:
	$(DOCKER_COMPOSE) build

rebuild:
	$(DOCKER_COMPOSE) build --no-cache

up:
	$(DOCKER_COMPOSE) up -d --build

up-logs:
	$(DOCKER_COMPOSE) up --build

up-dev:
	$(DOCKER_COMPOSE_DEV) up --build

up-tools:
	$(DOCKER_COMPOSE) --profile tools up -d --build

down:
	$(DOCKER_COMPOSE) down

down-volumes:
	$(DOCKER_COMPOSE) down -v

restart:
	$(DOCKER_COMPOSE) restart

logs:
	$(DOCKER_COMPOSE) logs -f

logs-api:
	$(DOCKER_COMPOSE) logs -f almanatura-api

logs-db:
	$(DOCKER_COMPOSE) logs -f almanatura-db

status:
	$(DOCKER_COMPOSE) ps

shell:
	$(DOCKER_COMPOSE) exec almanatura-api sh

db-shell:
	$(DOCKER_COMPOSE) exec almanatura-db sh -c 'mysql -u"$$MYSQL_USER" -p"$$MYSQL_PASSWORD" "$$MYSQL_DATABASE"'

test:
	./mvnw test

verify:
	./mvnw -B verify

format:
	./mvnw spotless:apply

format-check:
	./mvnw spotless:check

coverage:
	./mvnw -B verify
	@echo "Coverage report: target/site/jacoco/index.html"

db-backup:
	@mkdir -p backups
	$(DOCKER_COMPOSE) exec -T almanatura-db sh -c 'mysqldump -u$$MYSQL_USER -p$$MYSQL_PASSWORD $$MYSQL_DATABASE' \
	  > backups/almanatura_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Backup written to ./backups/"

db-restore:
	@test -n "$(FILE)" || (echo "Usage: make db-restore FILE=./backups/your.sql" && exit 1)
	cat $(FILE) | $(DOCKER_COMPOSE) exec -T almanatura-db sh -c 'mysql --default-character-set=utf8mb4 -uroot -p"$$MYSQL_ROOT_PASSWORD" "$$MYSQL_DATABASE"'

seed-demo:
	@test -f scripts/sql/seed_demo.sql || (echo "Missing scripts/sql/seed_demo.sql" && exit 1)
	cat scripts/sql/seed_demo.sql | $(DOCKER_COMPOSE) exec -T almanatura-db sh -c 'mysql --default-character-set=utf8mb4 -uroot -p"$$MYSQL_ROOT_PASSWORD" "$$MYSQL_DATABASE"'
	@echo "Demo seed applied (safe to re-run)."

wait-db:
	@attempt=1; \
	while ! $(DOCKER_COMPOSE) exec -T almanatura-db sh -c 'mysqladmin ping -h localhost -u root -p"$$MYSQL_ROOT_PASSWORD" >/dev/null 2>&1'; do \
		if [ $$attempt -ge 60 ]; then \
			echo "Database did not become ready in time"; \
			exit 1; \
		fi; \
		attempt=$$((attempt + 1)); \
	done

db-reset:
	$(DOCKER_COMPOSE) down -v --remove-orphans
	$(DOCKER_COMPOSE) up -d --build --wait
	@echo "Database reset complete."

db-reseed: db-reset
	$(MAKE) seed-demo
	@echo "Database reset and demo seed applied."

clean:
	$(DOCKER_COMPOSE) down --rmi local -v --remove-orphans
	docker system prune -f

# ---------- Production ----------
# All targets below merge docker-compose.yml + docker-compose.prod.yml.
# Run them ON THE SERVER, after copying .env.production.example to .env and
# filling in real secrets (chmod 600 .env).

prod-build:
	$(DOCKER_COMPOSE_PROD) build --pull

prod-up:
	$(DOCKER_COMPOSE_PROD) up -d --build

prod-up-logs:
	$(DOCKER_COMPOSE_PROD) up --build

prod-down:
	$(DOCKER_COMPOSE_PROD) down

prod-restart:
	$(DOCKER_COMPOSE_PROD) restart

prod-logs:
	$(DOCKER_COMPOSE_PROD) logs -f --tail=200

prod-status:
	$(DOCKER_COMPOSE_PROD) ps
