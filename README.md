# AlmaNatura API

[![CI](https://github.com/alexiscampusano/almanatura-api/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/alexiscampusano/almanatura-api/actions/workflows/ci.yml?query=branch%3Amain)

REST API powering Fundación AlmaNatura’s rural-actors platform: **projects** with a
mandatory **strategic pillar**, anonymous **applications**, an **actor directory** after
staff approval, and a JWT-protected back office. All API literals are **English**
(`ProjectPillar`, statuses, messages); the frontend owns localization.

## Strategic pillars (`ProjectPillar`)

Stable enum values everywhere (JSON, query params, database):

`TECHNOLOGY`, `EDUCATION`, `ENTREPRENEURSHIP`, `HEALTH`, `CULTURE`

## Project activities (`ProjectActivityStatus`)

`SCHEDULED`, `CANCELLED`, `COMPLETED` — milestones or encounters linked to a project (`project_activities`).

## Activity participation (`ActivityParticipationStatus`)

Staff-driven attendance workflow: `INVITED`, `CONFIRMED`, `DECLINED`, `ATTENDED`.

## Application workflow (`ApplicationStatus`)

`SUBMITTED` → `UNDER_REVIEW` → (`NEEDS_INFO` | `REJECTED` | `APPROVED`) →
`REGISTERED_AS_ACTOR` (creates `Actor`). Terminal: `REJECTED`, `REGISTERED_AS_ACTOR`.

## Tech stack

| Layer       | Tech                                      |
| ----------- | ----------------------------------------- |
| Language    | Java 21                                   |
| Framework   | Spring Boot 4.0.5 (Web MVC, Data JPA)     |
| Security    | Spring Security 7 + JJWT 0.12.6 (HS256)   |
| Database    | MySQL 8                                   |
| Docs        | Springdoc OpenAPI 3.0.3 (Swagger UI)      |
| Container   | Docker (multi-stage), docker compose      |
| Runtime user| Non-root `spring:spring`                  |

## Database migrations (Flyway)

Versioned SQL lives under [`src/main/resources/db/migration/`](src/main/resources/db/migration/).

**Legacy steps V2–V3** create tables from an older “cultural events” model. **V4** immediately drops
those tables and introduces the rural core (`projects`, `applications`, `actors`). The early scripts
stay in the repository so existing environments keep a **stable Flyway history and checksums**;
removing or rewriting V2–V3 would break `flyway validate` on databases that already applied them.
Greenfield runs still execute V2 → V3 → V4 → … so the drop leaves only the current schema.

## Branching model

This repository uses a two-branch flow tailored to a single backend
maintainer integrating against an external frontend team:

| Branch                 | Purpose                                                              | Stability                                                |
| ---------------------- | -------------------------------------------------------------------- | -------------------------------------------------------- |
| `main`                 | Stable surface consumed by the frontend. Tagged on every release.    | Production-grade. Only receives PRs from `develop`.      |
| `develop`              | Integration branch where features land first.                        | Should build & test green, but may carry partial work.   |
| `feat/*`, `chore/*`, `fix/*` | Short-lived branches for a single task or change.              | Volatile. Squash-merged into `develop` via PR.           |

**Day-to-day flow:** branch off `develop`, open a PR back into `develop`,
squash-merge once CI is green and the PR is reviewed.

**Releases:** when a coherent set of features is ready (for example,
auth + user management for v0.1.0), open a PR `develop → main`, merge it
with a real merge commit, and tag the resulting commit:

```bash
git checkout main && git pull
git tag -a v0.1.0 -m "Release v0.1.0 - auth and user management"
git push origin v0.1.0
```

The frontend should pin against tags or `main`, never against `develop`.

The CI badge above intentionally reports the status of `main` only:
`develop` is allowed to be momentarily red while a refactor is in flight.

## Prerequisites

- Docker + Docker Compose
- (Optional, for local non-docker runs) Java 21 and the bundled `mvnw`

## Quick start

```bash
git https://github.com/alexiscampusano/almanatura-api
cd almanatura-api
cp .env.example .env
# generate strong secrets and put them in .env:
#   openssl rand -base64 96      -> APP_JWT_SECRET    (HS512 needs >= 64 bytes)
#   openssl rand -base64 32      -> APP_ENCRYPTION_DNI_KEY
#
# APP_ADMIN_PASSWORD (initial super_user bootstrap + /auth/login) must be 12-100
# ASCII characters with at least one lowercase, one uppercase, one digit, and one
# special character from: !@#$%^&*()_+-=[]{}|;:,.? — see InternalPasswordPolicy in code.
```

Then pick the runtime that matches what you are doing — see
[Run modes (local / docker / production)](#run-modes-local--docker--production)
below for the full decision matrix.

Then:

| What           | URL                                                |
| -------------- | -------------------------------------------------- |
| API base       | http://localhost:8080/api/v1                       |
| Smoke test     | http://localhost:8080/api/v1/ping                  |
| Swagger UI     | http://localhost:8080/api/v1/swagger-ui.html       |
| OpenAPI JSON   | http://localhost:8080/api/v1/api-docs              |
| Health (actuator) | http://localhost:8080/api/v1/actuator/health    |
| phpMyAdmin (`make up-tools`) | http://localhost:8081                |

## API requests collection

A versioned Postman collection lives under [`postman/`](postman/) with the
`Almanatura — Local` environment, Bearer auth wired at the collection level
and a login Tests script that auto-stores the JWT for the rest of the
requests. See [`postman/README.md`](postman/README.md) for import and usage.

## Run modes (local / docker / production)

The project supports three runtimes. They are not alternatives — each one is
the right tool for a different situation. **You never need to edit the
`SPRING_PROFILES_ACTIVE` value when switching between modes**: each runtime
selects the correct Spring profile automatically.

| Mode | Spring profile | DB host | Where it runs | Use it when |
| ---- | -------------- | ------- | ------------- | ----------- |
| **A. Local (Maven)** | `dev` (from `.env`) | `localhost:3306` (Docker) | Your IDE / shell | Daily backend coding (hot reload + debugger) |
| **B. Docker dev** | `docker` (forced in compose) | `almanatura-db` (internal network) | Docker containers | Full-stack demo, frontend integration, no JDK needed |
| **C. Production** | `docker,prod` (forced in compose) | `almanatura-db` (internal network) | Server (VPS / cPanel) | Real deployment behind a reverse proxy |

### Mode A — Local backend dev (recommended for everyday coding)

Runs MySQL in Docker and the API directly on your machine via the Maven
wrapper. Spring Boot DevTools restarts the context on every save, the IDE
attaches the debugger natively, and there is no image rebuild loop.

```bash
docker compose up -d almanatura-db          # MySQL only
set -a; source .env; set +a                 # load env vars (one shell session)
./mvnw spring-boot:run                      # API on http://localhost:8080
```

That's it — no `SPRING_PROFILES_ACTIVE=...` override needed: `.env` already
sets it to `dev`, which targets `jdbc:mysql://localhost:3306/almanatura`.

> **Tests only:** `./mvnw test` (or `make test`). Uses H2 in memory, no
> MySQL or Docker required.

### Mode B — Full stack in Docker (no JDK on host)

Builds the multi-stage image and runs the API + MySQL together. Ideal for
onboarding, frontend integration or validating the image before deploying.

```bash
make up-logs        # attached, see logs in the terminal
# or
make up             # detached; then `make logs-api` to follow
```

The container forces `SPRING_PROFILES_ACTIVE=docker` regardless of what your
`.env` says, so you cannot accidentally launch it in dev mode.

| Tool | Command | URL |
| ---- | ------- | --- |
| phpMyAdmin (optional) | `make up-tools` | http://localhost:8081 |
| Remote debugger (JDWP) | `make up-dev` | port `5005` (mounts `./src` ro, DevTools on) |

### Mode C — Production deployment

Production uses an **override file** (`docker-compose.prod.yml`) on top of the
base `docker-compose.yml`. This override:

- forces `SPRING_PROFILES_ACTIVE=docker,prod` (Swagger off, Actuator hardened,
  no DevTools, no SQL echo);
- binds the API to `127.0.0.1:8080` only — your reverse proxy (nginx / Apache
  / Cloudflare Tunnel) is the single ingress and terminates TLS;
- removes the host port mapping for MySQL — only the API container can reach
  it, on the internal `almanatura-network`;
- sets `restart: always`, JSON-file log rotation and memory limits;
- excludes phpMyAdmin (the `tools` profile is never enabled).

#### One-time setup on the server

```bash
git clone <repo-url> && cd almanatura-api

# Production env file — never committed.
cp .env.production.example .env
chmod 600 .env

# Generate REAL secrets and paste them into .env:
openssl rand -base64 96      # -> APP_JWT_SECRET
openssl rand -base64 32      # -> APP_ENCRYPTION_DNI_KEY
openssl rand -base64 24      # -> MYSQL_PASSWORD and MYSQL_ROOT_PASSWORD

# Edit APP_CORS_ALLOWED_ORIGINS, APP_ADMIN_EMAIL, APP_ADMIN_PASSWORD too.
# APP_ADMIN_PASSWORD must satisfy the internal password policy (same as login);
# otherwise the API container will exit on bootstrap.
nano .env
```

#### Daily operations

```bash
make prod-up          # build + start (detached)
make prod-logs        # tail logs
make prod-status      # ps
make prod-restart     # rolling restart
make prod-down        # stop (volumes are kept)
```

…or the equivalent raw commands if you do not want to use `make`:

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
docker compose -f docker-compose.yml -f docker-compose.prod.yml logs -f --tail=200
docker compose -f docker-compose.yml -f docker-compose.prod.yml down
```

#### Why we don't use a `.env.dev` / `.env.prod` split

The `.env` file is gitignored and lives in **one** place: each environment
has exactly one (your laptop / the server). The "shape" of variables is the
same; only the values differ. We track two **templates** in git so you know
what to fill:

- `.env.example` – local development defaults (safe placeholder secrets)
- `.env.production.example` – production checklist (every secret marked
  `CHANGE_ME`, instructions on how to generate it)

Real secrets are never in the repository, in CI variables, or in chat. They
live in `/path/to/almanatura-api/.env` on the server, with permissions `600`.

## Available make targets

Run `make help` for the full list. Most useful:

```
# Dev / local
make build | rebuild
make up | up-logs | up-dev | up-tools
make down | down-volumes
make logs | logs-api | logs-db | status
make shell | db-shell
make test | verify | coverage
make format | format-check
make db-backup | db-restore FILE=...
make clean

# Production (uses docker-compose.prod.yml override)
make prod-build
make prod-up | prod-up-logs
make prod-down | prod-restart
make prod-logs | prod-status
```

## Project structure

The codebase follows a **layered / clean architecture**: HTTP layer (`controller`)
→ business logic (`service`) → persistence (`repository` + `entity`). Cross-cutting
concerns live in dedicated packages (`security`, `config`, `exception`, `util`,
`validation`). Each package has a single, well-defined responsibility — if a class does not fit
in any of them, it is probably doing too much.

```
src/
├── main/
│   ├── java/com/almanatura/api/
│   │   ├── AlmanaturaApiApplication.java       # Spring Boot entry point (@SpringBootApplication)
│   │   │
│   │   ├── bootstrap/
│   │   │   └── AdminBootstrapRunner.java       # Seeds initial SUPER_USER from env when configured
│   │   │
│   │   ├── config/
│   │   │   ├── AppProperties.java              # @ConfigurationProperties for app.* keys
│   │   │   ├── AuditorAwareConfig.java        # AuditorAware<String> for JPA auditing metadata
│   │   │   ├── CorsConfig.java                # CORS from APP_CORS_ALLOWED_ORIGINS
│   │   │   ├── OpenApiConfig.java             # Springdoc/OpenAPI + shared RFC 7807 schema bits
│   │   │   ├── PasswordEncoderConfig.java     # BCryptPasswordEncoder bean
│   │   │   └── SecurityConfig.java            # SecurityFilterChain — public routes vs JWT
│   │   │
│   │   ├── controller/
│   │   │   ├── AdminActivityParticipationController.java  # Nested …/activities/…/participations (JWT)
│   │   │   ├── AdminActorController.java      # GET /admin/actors
│   │   │   ├── AdminApplicationController.java # GET/PATCH /admin/applications
│   │   │   ├── AdminOutboundNotificationController.java   # POST /admin/notifications (stub)
│   │   │   ├── AdminProjectActivityController.java        # CRUD /admin/projects/{id}/activities
│   │   │   ├── AdminProjectController.java    # CRUD /admin/projects
│   │   │   ├── AdminProjectImpactController.java          # GET/POST …/projects/{id}/impact-entries
│   │   │   ├── AdminReportController.java     # GET …/reports/summary, …/projects/applications
│   │   │   ├── AdminUserController.java       # POST/GET /admin/users (SUPER_USER)
│   │   │   ├── ActorController.java           # Public GET /actors (?pillar=)
│   │   │   ├── ApplicationController.java     # Public POST /applications
│   │   │   ├── AuthController.java            # POST /auth/login, GET /auth/me
│   │   │   ├── HealthController.java          # GET /ping
│   │   │   └── ProjectController.java        # Public GET /projects, /{id}, /{id}/activities
│   │   │
│   │   ├── dto/
│   │   │   ├── ActivityParticipationResponse.java
│   │   │   ├── AdminApplicationResponse.java  # Decrypted national ID — internal only
│   │   │   ├── ApplicationSubmittedResponse.java
│   │   │   ├── CreateOutboundNotificationRequest.java
│   │   │   ├── CreateProjectActivityRequest.java
│   │   │   ├── CreateProjectImpactEntryRequest.java
│   │   │   ├── CreateProjectRequest.java
│   │   │   ├── CreateUserRequest.java
│   │   │   ├── InviteActivityParticipationRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── OutboundNotificationResponse.java
│   │   │   ├── PatchActivityParticipationRequest.java
│   │   │   ├── PatchApplicationStatusRequest.java
│   │   │   ├── ProjectActivityResponse.java
│   │   │   ├── ProjectApplicationReportRow.java
│   │   │   ├── ProjectImpactEntryResponse.java
│   │   │   ├── ProjectResponse.java
│   │   │   ├── ProjectStatusCount.java
│   │   │   ├── PublicActorResponse.java
│   │   │   ├── PublicProjectActivityResponse.java
│   │   │   ├── PublicProjectResponse.java
│   │   │   ├── ReportsSummaryResponse.java
│   │   │   ├── SubmitApplicationRequest.java
│   │   │   ├── UpdateProjectActivityRequest.java
│   │   │   ├── UpdateProjectRequest.java
│   │   │   └── UserSummary.java
│   │   │
│   │   ├── entity/
│   │   │   ├── ActivityParticipation.java
│   │   │   ├── Actor.java
│   │   │   ├── BaseAuditableEntity.java
│   │   │   ├── OutboundNotification.java
│   │   │   ├── Project.java
│   │   │   ├── ProjectActivity.java
│   │   │   ├── ProjectApplication.java
│   │   │   ├── ProjectImpactEntry.java
│   │   │   └── User.java
│   │   │
│   │   ├── enums/
│   │   │   ├── ActivityParticipationStatus.java
│   │   │   ├── ApplicationStatus.java
│   │   │   ├── NotificationChannel.java
│   │   │   ├── OutboundNotificationStatus.java
│   │   │   ├── ProjectActivityStatus.java
│   │   │   ├── ProjectPillar.java
│   │   │   ├── ProjectStatus.java
│   │   │   └── Role.java
│   │   │
│   │   ├── exception/
│   │   │   ├── ApiErrorWriter.java
│   │   │   ├── ApiProblems.java
│   │   │   ├── ApplicationAlreadyExistsException.java
│   │   │   ├── EmailAlreadyInUseException.java
│   │   │   ├── ErrorCode.java                 # Stable RFC7807 extension `code` values
│   │   │   ├── FieldViolation.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── InvalidApplicationTransitionException.java
│   │   │   ├── ParticipationAlreadyExistsException.java   # Maps to PARTICIPATION_ALREADY_EXISTS
│   │   │   ├── ProjectHasApplicationsException.java
│   │   │   └── ResourceNotFoundException.java
│   │   │
│   │   ├── mapper/
│   │   │   ├── package-info.java
│   │   │   ├── ProjectActivityMapper.java    # Activities, participation, notification & impact DTOs
│   │   │   └── ProjectMapper.java
│   │   │
│   │   ├── repository/
│   │   │   ├── ActivityParticipationRepository.java
│   │   │   ├── ActorRepository.java
│   │   │   ├── OutboundNotificationRepository.java
│   │   │   ├── ProjectActivityRepository.java
│   │   │   ├── ProjectApplicationRepository.java
│   │   │   ├── ProjectImpactEntryRepository.java
│   │   │   ├── ProjectRepository.java
│   │   │   └── UserRepository.java
│   │   │
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java   # Spring Security user lookup
│   │   │   ├── JwtAccessDeniedHandler.java     # 403 → ProblemDetail
│   │   │   ├── JwtAuthenticationEntryPoint.java # 401 → ProblemDetail
│   │   │   ├── JwtAuthenticationFilter.java    # Bearer extraction + validation
│   │   │   ├── JwtService.java                 # Sign/verify HS512 tokens (JJWT)
│   │   │   └── RateLimitFilter.java            # Bucket4j on /auth/login & POST /applications
│   │   │
│   │   ├── service/
│   │   │   ├── AdminActivityParticipationService.java
│   │   │   ├── AdminActorService.java
│   │   │   ├── AdminApplicationService.java
│   │   │   ├── AdminOutboundNotificationService.java
│   │   │   ├── AdminProjectActivityService.java
│   │   │   ├── AdminProjectImpactService.java
│   │   │   ├── AdminProjectService.java
│   │   │   ├── AdminReportService.java
│   │   │   ├── AdminUserService.java
│   │   │   ├── ApplicationStatusTransitions.java # Allowed PATCH transitions (domain guard)
│   │   │   ├── ApplicationSubmissionService.java
│   │   │   ├── AuthService.java
│   │   │   ├── PublicActorService.java
│   │   │   └── PublicProjectService.java
│   │   │
│   │   ├── validation/
│   │   │   ├── InternalPasswordPolicy.java     # Documents internal password rules
│   │   │   ├── StrongInternalPassword.java      # Bean Validation annotation for admin passwords
│   │   │   └── StrongInternalPasswordValidator.java
│   │   │
│   │   └── util/
│   │       └── DniCipherService.java           # AES-GCM for applicant national IDs
│   │
│   └── resources/
│       ├── application.properties              # Common defaults (active profile via env)
│       ├── application-dev.properties          # Local non-docker dev (verbose logs, MySQL)
│       ├── application-docker.properties       # Inside the container (host = almanatura-db)
│       ├── application-prod.properties         # cPanel / prod (no Swagger, ddl=validate)
│       └── db/migration/                       # Flyway versioned migrations (V1__*.sql, ...)
│
└── test/
    ├── java/com/almanatura/api/
    │   ├── AbstractIntegrationTest.java        # Optional MySQL Testcontainers base (@Profile integration)
    │   ├── AlmanaturaApiApplicationTests.java  # Context loads smoke test
    │   ├── architecture/
    │   │   └── ArchitectureTest.java           # ArchUnit layered rules
    │   ├── controller/
    │   │   ├── AdminActivityParticipationControllerTest.java
    │   │   ├── AdminApplicationControllerTest.java
    │   │   ├── AdminOutboundNotificationControllerTest.java
    │   │   ├── AdminProjectActivityControllerTest.java
    │   │   ├── AdminProjectControllerTest.java
    │   │   ├── AdminProjectImpactControllerTest.java
    │   │   ├── AdminReportControllerTest.java
    │   │   ├── AdminUserControllerTest.java
    │   │   ├── ApplicationControllerTest.java
    │   │   ├── AuthControllerTest.java
    │   │   └── ProjectControllerTest.java
    │   ├── exception/
    │   │   └── ErrorResponseTest.java          # MockMvc coverage for ProblemDetail responses
    │   └── validation/
    │       └── InternalPasswordPolicyTest.java
    └── resources/
        ├── application-test.properties         # H2 in-memory, used by @SpringBootTest
        └── application-integration.properties  # Reserved for Testcontainers (future)
```

### Conventions enforced by ArchUnit

`ArchitectureTest` is part of the build and will fail the pipeline if any of
these rules is violated. They exist so the layout above stays meaningful as
the codebase grows:

- `controller` may **only** depend on `service`, `dto`, `mapper`, `exception`.
- `service` may not depend on `controller`.
- `repository` may not depend on `controller` or `service`.
- `entity` may not depend on `controller`, `service`, `repository` or `dto`.
- No class outside `config` / `security` may import `org.springframework.security.*`.

**Layout note:** The codebase uses **package-by-layer** (`controller`, `service`, …), not
package-by-feature. That matches `ArchitectureTest` and is an intentional trade-off versus guides
that recommend feature packages; migrating would be a large refactor.

## Security model

Public endpoints (no JWT required):

- `GET  /api/v1/ping`
- `GET  /api/v1/projects`, `GET /api/v1/projects/{id}` — **PUBLISHED** projects only; optional `?pillar=` (`ProjectPillar`). List sorted by `startsAt` ascending. **`404`** `RESOURCE_NOT_FOUND` on detail if missing or not published.
- `GET  /api/v1/projects/{id}/activities` — schedule lines for a **PUBLISHED** project only (title, times, location, activity status); **`404`** if the project is not public.
- `GET  /api/v1/actors` — public directory (`fullName`, `region` only); optional `?pillar=`; includes actors with `REGISTERED_AS_ACTOR` applications on **PUBLISHED** projects for that pillar.
- `POST /api/v1/applications` — anonymous application to a **PUBLISHED** project; body: `projectId`, `fullName`, `email`, `dni`, optional `phone`; DNI encrypted at rest. **`201`** + `{ id, projectId, submittedAt }`. **`404`** if project missing/not published; **`409`** `APPLICATION_ALREADY_EXISTS` if the same email already applied to that project; **`429`** rate limit (same bucket family as documented for this path).
- `POST /api/v1/auth/login` — internal login
- `GET  /api/v1/swagger-ui/**`, `/api-docs/**`, `/actuator/health`

Authenticated endpoints (JWT in `Authorization: Bearer <token>`):

- `GET  /api/v1/auth/me` — current internal user (`SUPER_USER` or `EVENT_MANAGER`)
- `POST /api/v1/admin/users`, `GET /api/v1/admin/users` — `SUPER_USER` only
- `POST /api/v1/admin/projects` — create project (`DRAFT`); `SUPER_USER` or `EVENT_MANAGER`
- `GET  /api/v1/admin/projects` — list all projects (sorted by `startsAt`)
- `GET /api/v1/admin/projects/{id}`, `PUT /api/v1/admin/projects/{id}`, `DELETE /api/v1/admin/projects/{id}` — **`409`** `PROJECT_HAS_APPLICATIONS` on delete when rows still exist
- CRUD `/api/v1/admin/projects/{projectId}/activities` and `/api/v1/admin/projects/{projectId}/activities/{activityId}` — project schedule
- `GET|POST /api/v1/admin/projects/{projectId}/activities/{activityId}/participations`, `PATCH .../participations/{participationId}` — invite actors and update `ActivityParticipationStatus`; **`409`** `PARTICIPATION_ALREADY_EXISTS` when the actor is already on that activity
- `POST /api/v1/admin/notifications` — records a **`PENDING`** outbound notification row (stub; no SMTP/provider in this build)
- `GET|POST /api/v1/admin/projects/{projectId}/impact-entries` — lightweight impact metrics for follow-up / reporting
- `GET /api/v1/admin/applications` — optional `?projectId=&status=`
- `GET /api/v1/admin/applications/{id}`, `PATCH /api/v1/admin/applications/{id}` — status transitions; **`400`** `INVALID_APPLICATION_TRANSITION` when illegal; `REGISTERED_AS_ACTOR` creates `Actor`
- `GET /api/v1/admin/actors`, `GET /api/v1/admin/actors/{id}`
- `GET /api/v1/admin/reports/summary` — counts per `ProjectStatus`, total projects and applications, plus planning/follow-up rollups: total project activities, activity participations, impact entries, and outbound notification rows (counts only; no applicant PII)
- `GET /api/v1/admin/reports/projects/applications` — each project with `applicationCount`, ordered by count desc then `startsAt`
- `/api/v1/admin/**` — `SUPER_USER` or `EVENT_MANAGER` except `/admin/users/**` (super only)

Passwords are hashed with BCrypt. Sessions are stateless. CORS origins are
controlled by the `APP_CORS_ALLOWED_ORIGINS` env var.

## Sensitive data

National ID numbers (`dni`) on **applications** are stored encrypted at rest (AES-256-GCM).
Use `DniCipherService` in services; never persist cleartext. Public listings **never** return
decrypted PII. Admin application responses may include decrypted values for authorized staff only.
Rotate `APP_ENCRYPTION_DNI_KEY` only with a documented re-encryption migration.

## Error response format (RFC 7807)

Every error returned by the API follows
[RFC 7807 Problem Details](https://www.rfc-editor.org/rfc/rfc7807) with two
AlmaNatura extensions: a stable, machine-readable `code` and the OpenTelemetry
`traceId` of the request. The `Content-Type` is always
`application/problem+json`.

Example payload (`POST /api/v1/admin/projects` with an invalid body):

```json
{
  "type": "https://almanatura.org/errors/validation-failed",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields are invalid",
  "instance": "/api/v1/admin/projects",
  "code": "VALIDATION_FAILED",
  "traceId": "65c2f4a1b8d3e7f9a0b1c2d3e4f5a6b7",
  "timestamp": "2026-04-19T20:14:33.421Z",
  "violations": [
    { "field": "title", "message": "must not be blank" },
    { "field": "date",  "message": "must be a future date" }
  ]
}
```

### Catalog of `code`s

| `code`                      | HTTP | When it appears                                           |
| --------------------------- | ---- | --------------------------------------------------------- |
| `VALIDATION_FAILED`         | 400  | Bean Validation (`@Valid`) failed; see `violations[]`     |
| `EMAIL_ALREADY_IN_USE`      | 409  | Email already registered (e.g. `POST /admin/users`)       |
| `APPLICATION_ALREADY_EXISTS` | 409 | Same email twice for the same project (`POST /applications`) |
| `PARTICIPATION_ALREADY_EXISTS` | 409 | Duplicate invite for the same activity + actor (`POST .../participations`) |
| `INVALID_APPLICATION_TRANSITION` | 400 | Illegal `PATCH /admin/applications/{id}` status change |
| `PROJECT_HAS_APPLICATIONS` | 409 | `DELETE /admin/projects/{id}` while applications exist   |
| `MALFORMED_REQUEST`         | 400  | Body cannot be parsed (invalid JSON, type mismatch, etc.) |
| `MISSING_PARAMETER`         | 400  | Required query/path parameter is absent                   |
| `TYPE_MISMATCH`             | 400  | Parameter value cannot be converted to the declared type  |
| `INVALID_CREDENTIALS`       | 401  | Wrong email/password on `/auth/login`                     |
| `AUTHENTICATION_REQUIRED`   | 401  | No (or invalid) JWT on a protected endpoint               |
| `ACCESS_DENIED`             | 403  | Authenticated user lacks the required role/authority      |
| `RESOURCE_NOT_FOUND`        | 404  | Endpoint or domain object does not exist                  |
| `METHOD_NOT_ALLOWED`        | 405  | HTTP method not supported by the endpoint                 |
| `MEDIA_TYPE_NOT_SUPPORTED`  | 415  | `Content-Type` not accepted by the endpoint               |
| `RATE_LIMIT_EXCEEDED`       | 429  | Bucket4j throttle on `/auth/login` or `POST /applications` (`Retry-After` header) |
| `INTERNAL_ERROR`            | 500  | Unhandled exception; details in server logs               |

### Frontend integration guide

- **Switch on `code`, never on `detail`.** `detail` is a human-readable string
  intended for support / logs; localized UI messages are derived from `code`.
- **Use `traceId` in support requests.** Backend logs include the same value
  via MDC (`logging.pattern.level`), so on-call can pinpoint the request
  instantly.
- **Validation errors:** when `code === "VALIDATION_FAILED"`, render
  field-level messages from `violations[]` next to the corresponding inputs.
- **Auth lifecycle:** treat `AUTHENTICATION_REQUIRED` as "force re-login" and
  `INVALID_CREDENTIALS` as "show error inside the login form" — never confuse
  them.
- **Rate limit:** when receiving `429`, respect the `Retry-After` header (in
  seconds) before allowing a retry.

The full schema is published on Swagger UI for every operation under
`/api/v1/swagger-ui.html`.

## Production checklist

The recommended deployment path is the Docker stack with the production
override — see [Mode C — Production deployment](#mode-c--production-deployment)
above for the actual commands. Pre-flight checklist:

1. Server has Docker + Docker Compose installed.
2. `.env` exists at the project root with permissions `600`, populated from
   `.env.production.example` with **freshly generated secrets** (never the
   placeholders, never the dev values).
3. `APP_CORS_ALLOWED_ORIGINS` lists only the public frontend origin (HTTPS).
4. A reverse proxy (nginx / Apache / Cloudflare Tunnel) terminates TLS and
   forwards to `127.0.0.1:${API_PORT}` — the API is not exposed to the
   internet directly.
5. After `make prod-up`, `GET /api/v1/actuator/health` returns
   `{"status":"UP"}` (proxy it through your reverse proxy for the real check).
6. Set up a recurring `make db-backup` (cron / systemd timer) and verify a
   restore on a staging copy.

### Alternative: bare-metal jar (cPanel "Setup Java App")

If your hosting only allows running a jar directly (no Docker), the same
profile system works:

1. `./mvnw -DskipTests package` (locally or in CI) → `target/api-0.0.1-SNAPSHOT.jar`.
2. Provision a MySQL database in cPanel; record host, database, user, password.
3. On the server, export every `APP_*` and `SPRING_DATASOURCE_*` env var, plus
   `SPRING_PROFILES_ACTIVE=prod`.
4. Run with `java -jar api-0.0.1-SNAPSHOT.jar` (cPanel "Setup Java App" or a
   systemd unit).
5. Same health check + CORS rule as above.

## Security notes

- **Never commit `.env`.** It is git-ignored; only `.env.example` is tracked.
- Rotate `APP_JWT_SECRET` and `APP_ENCRYPTION_DNI_KEY` on every environment
  promotion and on any suspected leak.
- Production runs `ddl-auto=validate`; schema changes go through migrations
  or manual DDL, never through Hibernate auto-update.
- The `dev` profile is for development only — it enables verbose logs,
  hot-reload and the JDWP debugger.

## License

Private — Fundación AlmaNatura.
