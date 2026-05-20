# AlmaNatura API

[![CI](https://github.com/alexiscampusano/almanatura-api/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/alexiscampusano/almanatura-api/actions/workflows/ci.yml?query=branch%3Amain)

REST API powering Fundación AlmaNatura’s rural-actors platform: **projects** with a
mandatory **strategic pillar**, anonymous **applications**, an **actor directory** after
staff approval, and a JWT-protected back office. All API literals are **English**
(`ProjectPillar`, statuses, messages); the frontend owns localization.

## Strategic pillars (`ProjectPillar`)

Stable enum values everywhere (JSON, query params, database):

`TECHNOLOGY`, `EDUCATION`, `ENTREPRENEURSHIP`, `HEALTH`, `CULTURE`

## Application workflow (`ApplicationStatus`)

`SUBMITTED` → `UNDER_REVIEW` → (`NEEDS_INFO` | `REJECTED` | `APPROVED`) →
`REGISTERED_AS_ACTOR` (creates `Actor`). Terminal: `REJECTED`, `REGISTERED_AS_ACTOR`.

## Tech stack

| Layer       | Tech                                      |
| ----------- | ----------------------------------------- |
| Language    | Java 25                                   |
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
### Database Schema Notes

**Legacy tables** (`cultural_events`, `event_attendees`) were created in V2–V3 but are not mapped to any JPA entity or accessed by controllers/services. They remain in the database for backward compatibility and stable Flyway checksums but are effectively unused. The `project_activities` and `activity_participations` tables were similarly created (V5–V6) and dropped (V9) during domain evolution. When in doubt about table usage, check `src/main/java/com/almanatura/api/entity/` for mapped JPA entities.
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
- (Optional, for local non-docker runs) Java 25 and the bundled `mvnw`

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

The repository is organized around one base compose file plus two overrides.
Each one serves a different operating mode and the `Makefile` wraps them so
you do not have to remember the exact compose flags.

| Mode | Files | What it does | Typical use |
| ---- | ----- | ------------ | ----------- |
| **Local backend dev** | `./mvnw` + `docker compose up -d almanatura-db` | Runs the API on your machine and MySQL in Docker | Day-to-day coding with IDE debugging |
| **Docker dev** | `docker-compose.yml` + `docker-compose.dev.yml` | Runs the API from source inside a dev container with JDK/Maven and JDWP | Full-stack work, parity checks, onboarding |
| **Production** | `docker-compose.yml` + `docker-compose.prod.yml` | Runs the hardened runtime image with internal-only DB and production bindings | VPS/server deployments |

### Local backend dev

This is still the recommended workflow when you are actively coding the API.
MySQL runs in Docker, while Spring Boot runs on the host so DevTools and the IDE
work with the least friction.

```bash
docker compose up -d almanatura-db
set -a; source .env; set +a
./mvnw spring-boot:run
```

`SPRING_PROFILES_ACTIVE` already comes from `.env` and points to `dev`, which
matches `src/main/resources/application-dev.properties`.

> Tests remain separate: `./mvnw test` or `make test` uses H2 in memory.

### Docker dev

Use `make up-dev` when you want the application running inside Docker but still
in a development-friendly environment. The dev compose file builds the `dev`
stage of the `Dockerfile`, mounts the repository, exposes the JDWP port, and
keeps the database on the same compose network.

```bash
make up-dev
```

This mode is useful for frontend integration, checking container behavior, or
working on a machine without a local JDK. You still edit code on the host; the
container runs the application from source.

### Production

Production uses the base compose file plus `docker-compose.prod.yml`. MySQL is
not published on the host, the API binds to localhost unless you place a proxy
in front of it, and the stack uses restart policies plus resource limits.

```bash
make prod-up
make prod-logs
make prod-status
make prod-restart
make prod-down
```

If you prefer raw Compose commands:

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
docker compose -f docker-compose.yml -f docker-compose.prod.yml logs -f --tail=200
docker compose -f docker-compose.yml -f docker-compose.prod.yml down
```

#### Demo data and templates

Demo actors and published projects are optional. After the schema exists,
load them with `make seed-demo` against either the local or Docker dev stack.
`make db-restore` and `make seed-demo` both force `--default-character-set=utf8mb4`
so UTF-8 SQL stays intact.

If you need to completely reset a local database, use:

```bash
make db-reset      # drop volumes and bring the stack back with an empty DB
make db-reseed     # reset the database and load the demo data again
```

`make db-reset` is the clean starting point for a fresh environment. `make db-reseed`
wraps the reset and then loads [`scripts/sql/seed_demo.sql`](scripts/sql/seed_demo.sql),
which is idempotent, so you can rerun it safely without duplicating rows.

The repository keeps two templates only:

- `.env.example` for local development defaults
- `.env.production.example` for the production checklist

Real secrets live only in your local `.env` file or the server `.env` file.

## Available make targets

Run `make help` for the full list. The most useful commands are:

```bash
# Base / local
make build | rebuild
make up | up-logs
make down | down-volumes | restart
make logs | logs-api | logs-db | status
make shell | db-shell
make test | verify | coverage
make format | format-check
make db-backup | db-restore FILE=...
make seed-demo
make clean

# Docker dev
make up-dev

# Production
make prod-build
make prod-up | prod-up-logs
make prod-down | prod-restart
make prod-logs | prod-status
```


→ business logic (`service`) → persistence (`repository` + `entity`). Cross-cutting
concerns live in dedicated packages (`security`, `config`, `exception`, `util`,
in any of them, it is probably doing too much.
```
src/
│   ├── java/com/almanatura/api/
│   │   ├── AlmanaturaApiApplication.java       # Spring Boot entry point (@SpringBootApplication)
│   │   │
│   │   ├── bootstrap/
│   │   │   └── AdminBootstrapRunner.java       # SUPER_USER from env on startup (dev/docker)
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
│   │   │   ├── AdminActorController.java      # GET /admin/actors, GET /admin/actors/{id}
│   │   │   ├── AdminApplicationController.java # GET/GET/{id}/PATCH /admin/applications
│   │   │   ├── AdminOutboundNotificationController.java   # POST /admin/notifications (stub)
│   │   │   ├── AdminProjectController.java    # CRUD /admin/projects
│   │   │   ├── AdminProjectImpactController.java          # GET/POST …/projects/{id}/impact-entries
│   │   │   ├── AdminReportController.java     # GET …/reports/summary, …/projects/applications
│   │   │   ├── AdminUserController.java       # POST/GET /admin/users (SUPER_USER)
│   │   │   ├── ApplicationController.java     # Public POST /applications
│   │   │   ├── AuthController.java            # POST /auth/login, GET /auth/me
│   │   │   ├── HealthController.java          # GET /ping
│   │   │   └── ProjectController.java         # Public GET /projects, /{id}
│   │   │
│   │   ├── dto/
│   │   │   ├── AdminApplicationResponse.java  # Decrypted national ID — internal only
│   │   │   ├── ApplicationSubmittedResponse.java
│   │   │   ├── CreateOutboundNotificationRequest.java
│   │   │   ├── CreateProjectImpactEntryRequest.java
│   │   │   ├── CreateProjectRequest.java
│   │   │   ├── CreateUserRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── OutboundNotificationResponse.java
│   │   │   ├── PatchApplicationStatusRequest.java
│   │   │   ├── ProjectApplicationReportRow.java
│   │   │   ├── ProjectImpactEntryResponse.java
│   │   │   ├── ProjectResponse.java
│   │   │   ├── ProjectStatusCount.java
│   │   │   ├── PublicActorResponse.java
│   │   │   ├── PublicProjectResponse.java
│   │   │   ├── ReportsSummaryResponse.java
│   │   │   ├── SubmitApplicationRequest.java
│   │   │   ├── UpdateProjectRequest.java
│   │   │   └── UserSummary.java
│   │   │
│   │   ├── entity/
│   │   │   ├── Actor.java
│   │   │   ├── BaseAuditableEntity.java
│   │   │   ├── OutboundNotification.java
│   │   │   ├── Project.java
│   │   │   ├── ProjectApplication.java
│   │   │   ├── ProjectImpactEntry.java
│   │   │   └── User.java
│   │   │
│   │   ├── enums/
│   │   │   ├── ApplicationStatus.java
│   │   │   ├── NotificationChannel.java
│   │   │   ├── OutboundNotificationStatus.java
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
│   │   │   ├── ProjectHasApplicationsException.java
│   │   │   └── ResourceNotFoundException.java
│   │   │
│   │   ├── mapper/
│   │   │   ├── package-info.java
│   │   │   └── ProjectMapper.java
│   │   │
│   │   ├── repository/
│   │   │   ├── ActorRepository.java
│   │   │   ├── OutboundNotificationRepository.java
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
│   │   │   ├── AdminActorService.java
│   │   │   ├── AdminApplicationService.java
│   │   │   ├── AdminOutboundNotificationService.java
│   │   │   ├── AdminProjectImpactService.java
│   │   │   ├── AdminProjectService.java
│   │   │   ├── AdminReportService.java
│   │   │   ├── AdminUserService.java
│   │   │   ├── ApplicationStatusTransitions.java # Allowed PATCH transitions (domain guard)
│   │   │   ├── ApplicationSubmissionService.java
│   │   │   ├── AuthService.java
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
    │   │   ├── AdminApplicationControllerTest.java
    │   │   ├── AdminOutboundNotificationControllerTest.java
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
- `GET  /api/v1/projects`, `GET /api/v1/projects/{id}` — **PUBLISHED** projects only; optional `?pillar=` (`ProjectPillar`). List sorted by `startsAt` ascending. Response includes `imageUrl` (nullable). **`404`** `RESOURCE_NOT_FOUND` on detail if missing or not published.
- `POST /api/v1/applications` — anonymous application to a **PUBLISHED** project; body: `projectId`, `fullName`, `email`, `dni`, optional `phone`; DNI encrypted at rest. **`201`** + `{ id, projectId, submittedAt }`. **`404`** if project missing/not published; **`409`** `APPLICATION_ALREADY_EXISTS` if the same email already applied to that project; **`429`** rate limit (same bucket family as documented for this path).
- `POST /api/v1/auth/login` — internal login
- `GET  /api/v1/swagger-ui/**`, `/api-docs/**`, `/actuator/health`

Authenticated endpoints (JWT in `Authorization: Bearer <token>`):

- `GET  /api/v1/auth/me` — current internal user (`SUPER_USER` or `EVENT_MANAGER`)
- `POST /api/v1/admin/users`, `GET /api/v1/admin/users` — `SUPER_USER` only
- `POST /api/v1/admin/projects` — create project (`DRAFT`); `SUPER_USER` or `EVENT_MANAGER`. Body includes optional `imageUrl` (max 512 chars).
- `GET  /api/v1/admin/projects` — list all projects (sorted by `startsAt`)
- `GET /api/v1/admin/projects/{id}`, `PUT /api/v1/admin/projects/{id}`, `DELETE /api/v1/admin/projects/{id}` — PUT accepts optional `imageUrl`. **`409`** `PROJECT_HAS_APPLICATIONS` on delete when rows still exist
- `POST /api/v1/admin/notifications` — records a **`PENDING`** outbound notification row (stub; no SMTP/provider in this build)
- `GET|POST /api/v1/admin/projects/{projectId}/impact-entries` — lightweight impact metrics for follow-up / reporting
- `GET /api/v1/admin/applications` — optional `?projectId=&status=`
- `GET /api/v1/admin/applications/{id}`, `PATCH /api/v1/admin/applications/{id}` — status transitions; **`400`** `INVALID_APPLICATION_TRANSITION` when illegal; `REGISTERED_AS_ACTOR` creates `Actor`
- `GET /api/v1/admin/actors`, `GET /api/v1/admin/actors/{id}`
- `GET /api/v1/admin/reports/summary` — counts per `ProjectStatus`, total projects, applications, impact entries and outbound notification rows (counts only; no applicant PII)
- `GET /api/v1/admin/reports/projects/applications` — each project with `applicationCount`, ordered by count desc then `startsAt`
- `/api/v1/admin/**` — `SUPER_USER` or `EVENT_MANAGER` except `/admin/users/**` (super only)

Passwords are hashed with BCrypt. Sessions are stateless. CORS origins are
controlled by the `APP_CORS_ALLOWED_ORIGINS` env var.

### Hardening and deployment notes

- **Rate-limit client identity**: By default (`APP_RATELIMIT_TRUST_FORWARDED_HEADERS=false`), login and `POST /applications` buckets use **only** the servlet remote address, so arbitrary clients cannot spoof `X-Forwarded-For` to bypass limits. Set **`APP_RATELIMIT_TRUST_FORWARDED_HEADERS=true`** only when every request passes through a **trusted** reverse proxy that controls forwarded headers.
- **Horizontal scaling**: In-memory Bucket4j counters are **not** shared across JVM replicas; enforce limits at an edge gateway/WAF or move buckets to a shared store (e.g. Redis).
- **JWT lifecycle**: Tokens remain valid until expiry unless you add revocation (denylist, shorter TTL + refresh, or key rotation with a planned logout). Prefer a **shorter `APP_JWT_EXPIRATION_MS`** in production when product constraints allow; rotating **`APP_JWT_SECRET`** invalidates all outstanding tokens—plan explicitly.
- **HTTP security headers**: The API sends baseline headers (e.g. `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, CSP safe for a JSON-only surface, `Permissions-Policy`). Terminate TLS at the proxy or JVM and prefer **HSTS at the edge** when serving HTTPS.
- **Forwarded headers**: If you enable `server.forward-headers-strategy` behind a proxy, read Spring Boot’s guidance so scheme/host/client IP match reality; combine with the rate-limit trust flag above.

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
    { "field": "startsAt",  "message": "must not be blank" }
  ]
}
```

### Catalog of `code`s

| `code`                      | HTTP | When it appears                                           |
| --------------------------- | ---- | --------------------------------------------------------- |
| `VALIDATION_FAILED`         | 400  | Bean Validation (`@Valid`) failed; see `violations[]`     |
| `EMAIL_ALREADY_IN_USE`      | 409  | Email already registered (e.g. `POST /admin/users`); response omits the address |
| `APPLICATION_ALREADY_EXISTS` | 409 | Same email twice for the same project (`POST /applications`) |
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
   `SPRING_PROFILES_ACTIVE=prod`. Ensure `SPRING_DATASOURCE_URL` includes
   `useUnicode=true` and `characterEncoding=UTF-8` (same idea as in
   `application-dev.properties` / `docker-compose.yml`) so Spanish text is not
   corrupted at the JDBC layer.
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
