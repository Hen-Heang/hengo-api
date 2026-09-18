# Deployment Readiness Runbook

## Purpose

Use this runbook to verify that KoriAI Backend is not only running, but is ready to serve requests that depend on PostgreSQL.

## Health endpoints

### Liveness

`GET /api/health`

Confirms that the Spring Boot process is responding. A successful response returns HTTP `200` with `data.status = "UP"`.

### Readiness

`GET /api/health/ready`

Confirms that the application can obtain a valid PostgreSQL connection.

Expected states:

| PostgreSQL | HTTP | `data.status` | `data.database` |
|---|---:|---|---|
| Available | 200 | `READY` | `UP` |
| Unavailable | 503 | `NOT_READY` | `DOWN` |

Railway uses `/api/health/ready` as its deployment health check, configured in `railway.toml`.

## Production verification

After a deployment:

1. Confirm the deployment reaches a successful state in Railway.
2. Call `GET https://<service-domain>/api/health` and expect HTTP 200.
3. Call `GET https://<service-domain>/api/health/ready` and expect HTTP 200 with `READY` and database `UP`.
4. If liveness succeeds but readiness returns 503, investigate PostgreSQL connectivity and datasource environment variables before application code.
5. Do not treat a running JVM as a healthy deployment when readiness is failing.

Example:

```bash
curl -i https://<service-domain>/api/health/ready
```

## Failure triage

When readiness is `NOT_READY`, check in this order:

1. Railway PostgreSQL service is running.
2. `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` are present for the backend service.
3. JDBC URL points to the intended PostgreSQL database/environment.
4. Backend deployment logs contain no datasource authentication, DNS, connection-refused, or timeout errors.
5. Retry readiness only after the infrastructure/configuration issue is corrected.

## CI relationship

GitHub Actions runs the Maven test suite with a PostgreSQL service. CI proves the code and database integration pass in an isolated test environment; the readiness endpoint proves the deployed application can reach its actual runtime database. Both checks are needed for deployment confidence.

## Learning note

**Liveness and readiness answer different operational questions.** Liveness asks whether the application process responds. Readiness asks whether the application can currently serve dependency-backed traffic. Keeping these separate prevents traffic from being routed to an app whose JVM is alive while PostgreSQL is unavailable.

## Next smallest task

Verify `/api/health/ready` against the real Railway production service domain and record the HTTP status plus `status`/`database` fields.