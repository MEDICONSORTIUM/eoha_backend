# EOHA Backend — Notes

## Current state

A placeholder REST API: canned sample data in the final response shape, no database.
It exists so the frontend can be built against a stable contract before the real data
source is decided.

From Trevor's review, what carried over:

- **DTOs** — controllers return `ZoneResponse` / `MalariaObservationResponse` records,
  never entities. `ApiResponse` envelope + `PagedResponse` for lists.
- **Global exception handling** — `GlobalExceptionHandler` (`@RestControllerAdvice`):
  unknown id → 404, bad path param → 400, anything else → 500, all in the `ApiResponse`
  shape.
- **One endpoint per resource with query params** — `GET /api/v1/zones?province=&municipality=&page=&size=`
  and `GET /api/v1/malaria-observations?zoneId=&date=&page=&size=`, instead of a route
  per filter.
- **`/api/v1` versioning** — resource routes are under `/api/v1`.
- **CORS** — `WebConfig` allows `GET`/`HEAD` on `/api/**` from any local origin;
  `CORS_ALLOWED_ORIGINS` overrides for production.

Removed: JPA/Hibernate, Spring Data, Flyway, the PostgreSQL driver, the entity and
repository classes, the DB migration, `application.properties` datasource config, and
the deprecated path-variable controllers. Trevor's point about database indexing is
moot until there is a database.

## When a real data source is chosen

- Replace the `List.of(...)` in `ZoneService` / `MalariaObservationService` with real
  lookups; keep the method signatures so controllers don't change.
- If it's a relational DB: re-add `spring-boot-starter-data-jpa` + driver, re-add the
  entities, and add indexes on the filtered columns (`province_name`,
  `municipality_name`, `zone_id`, `date_recorded`) via Flyway — note that Spring Boot
  4.x needs `org.springframework.boot:spring-boot-flyway` for Flyway to auto-run, and
  PostgreSQL needs `CAST(:param AS <type>)` on any parameter used only in an `IS NULL`
  test.
- Add a test suite (there is none): service unit tests + `@WebMvcTest` slice tests.
- Trim `boundaryPolygon` from list responses if it turns out to be large.
