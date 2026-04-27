# AlmaNatura API — Postman collection

Versioned Postman v2.1.0 collection plus a local environment for exercising the AlmaNatura backend without hand-crafting `curl` calls.

```
postman/
├── almanatura-api.postman_collection.json    # the collection (folders + requests + auth wiring)
└── almanatura-api.postman_environment.json   # the "Almanatura — Local" environment
```

## What's inside

The collection is organised by epic so each upcoming task has an obvious home:

| Folder            | Purpose                                                     | Status                                                              |
| ----------------- | ----------------------------------------------------------- | ------------------------------------------------------------------- |
| `Health`          | `GET /ping` liveness probe.                                 | Ready to use today.                                                 |
| `Auth`            | `POST /auth/login` (JWT) and `GET /auth/me` (profile, Bearer). | Ready: login + current user profile.                               |
| `Users (admin)`   | `POST /admin/users`, `GET /admin/users` (Bearer, super_user). | Ready: create + list internal users.                                |
| `Events (admin)`  | `POST/GET /admin/events`, `GET .../events/{id}` (Bearer).   | Task 14: create + read; Task 15: update + delete.                   |
| `Events (public)` | Public read-only feed used by the website.                  | Empty, populated in Task 18.                                        |
| `Attendees`       | Public registration + admin attendee listing with DNI decryption. | Empty, populated in Tasks 22 and 26.                          |
| `Reports`         | Aggregated history and reports.                             | Empty, populated in Task 25.                                        |

## How to import

1. Open Postman → **File → Import**.
2. Drop both files (`almanatura-api.postman_collection.json` and `almanatura-api.postman_environment.json`) onto the import dialog.
3. In the top-right environment selector pick **Almanatura — Local**.

## How authentication works

The collection has Bearer auth configured at the root level pointing to the `accessToken` collection variable. Each request inherits it unless it explicitly opts out (the `Health` and `Auth` requests use `noauth`).

Workflow:

1. Open **Auth → POST login**.
2. Set `adminPassword` in the environment (top-right gear icon → "Almanatura — Local"). It must match `APP_ADMIN_PASSWORD` in your `.env` and meet the API policy (12-100 chars, mixed case, digit, allowed special — see README / `InternalPasswordPolicy`). Leave it as `secret`. **Do not commit a value to git.**
3. Hit **Send**. The Tests script captures the JWT and stores it in `pm.collectionVariables.accessToken`.
4. Every other request now authenticates automatically.

When the token expires (default 24h, see `APP_JWT_EXPIRATION_MS`) just run **POST login** again.

## Environment variables

| Variable        | Type    | Default                | Notes                                                                                              |
| --------------- | ------- | ---------------------- | -------------------------------------------------------------------------------------------------- |
| `host`          | default | `http://localhost:8080`| Adjust when targeting a deployed instance behind a reverse proxy.                                  |
| `contextPath`   | default | `/api/v1`              | Mirrors `server.servlet.context-path` from `src/main/resources/application.properties`.            |
| `accessToken`   | secret  | empty                  | Filled automatically by the login Tests script. Never commit a value.                              |
| `adminEmail`    | default | `admin@almanatura.org` | Matches `APP_ADMIN_EMAIL` from `.env`.                                                              |
| `adminPassword` | secret  | empty                  | Each developer pastes their own value locally. **Never commit a value.**                            |

The collection itself derives `baseUrl = {{host}}{{contextPath}}`, so requests reference `{{baseUrl}}/...` and you only ever change `host` when pointing to a different environment.

## Running against another environment

Duplicate `Almanatura — Local`, name it `Almanatura — Staging` (or similar), and override `host` (for example `https://api-staging.almanatura.org`) and any credentials you need. Keep secrets out of git: only the local template is versioned.

## Pitfalls

- **`401 Unauthorized` on protected requests** — your `accessToken` is empty or expired. Run `Auth → POST login`.
- **`429 Too Many Requests` on login** — the rate-limit filter (`RateLimitFilter`, Bucket4j) is throttling that client IP. Wait the configured window or relax `APP_RATELIMIT_LOGIN_*` for local development.
- **Connection refused** — the API is not running. Check `make up` (Docker) or `make run` (local Maven) and that `host` in the environment matches.
