# EOHA Backend — Improvements

This tracks the enterprise-review feedback from Trevor Matlhaku and the related gaps
identified alongside it, and what happened to each on `feature/api-v1-and-dashboard`.

---

## Addressed in `feature/api-v1-and-dashboard`

### 1. Response DTOs

**Was**: Controllers returned `Zone` / `MalariaObservation` JPA entities directly inside `ApiResponse`, coupling the JSON contract to the database schema.

**Now**: `dto/ZoneResponse.java` and `dto/MalariaObservationResponse.java` (records) mirror the API contract independently of the entities, with a static `from(entity)` factory. Services map entity → DTO; controllers only ever see DTOs. `dto/PagedResponse.java` does the same for paginated lists, so the JSON shape doesn't depend on Spring Data's internal `PageImpl` serialization.

### 2. Global exception handling

**Was**: No committed global handler; services threw bare `new RuntimeException("... not found")`.

**Now**: `exception/ResourceNotFoundException.java` is thrown by the services for missing entities and mapped to HTTP 404 by `exception/GlobalExceptionHandler.java` (`@RestControllerAdvice`). The previous blanket `RuntimeException → 404` mapping was removed — an unexpected `RuntimeException` (e.g. a bug) now correctly falls through to the generic `Exception → 500` handler instead of masquerading as "not found". Added a `MethodArgumentNotValidException → 400` handler for future bean-validation use, and SLF4J logging (WARN on 404, ERROR with stack trace on 500). `Agents.md`'s documented error behavior was corrected from 500 to 404 for not-found.

### 3 & 4. Consolidated, versioned endpoints

**Was**: One endpoint per filter (`/province/{p}`, `/municipality/{m}`, `/zone/{id}`, `/date/{d}`, `/zone/{id}/date/{d}`), unversioned (`/api/zones`, `/api/malaria-observations`).

**Now**: `controller/ZoneV1Controller.java` and `controller/MalariaObservationV1Controller.java` expose one `GET` per resource under `/api/v1/`, with all filters as optional query parameters (`?province=&municipality=`, `?zoneId=&date=`) plus pagination (`?page=&size=`, default size 20). Each repository has a single null-guarded `@Query` (`WHERE (:x IS NULL OR field = :x) AND ...`) backing it, replacing the per-criterion derived finders.

The pre-versioning routes (`ZoneController`, `MalariaObservationController`) are kept as **deprecated aliases** — annotated `@Deprecated`, delegating to the same service methods with `Pageable.unpaged()`, returning the same unwrapped list shape as before so existing consumers aren't broken. They're slated for removal in a future major version.

### 5. Database indexes

**Was**: No indexes on filtered columns; `ddl-auto=none` + no migration tool meant `@Index` entity annotations would have been inert.

**Now**: Flyway (`flyway-core` + `flyway-database-postgresql`) manages schema migrations. `src/main/resources/db/migration/V1__add_search_indexes.sql` adds:

```sql
CREATE INDEX IF NOT EXISTS idx_zone_province_name ON zone (province_name);
CREATE INDEX IF NOT EXISTS idx_zone_municipality_name ON zone (municipality_name);
CREATE INDEX IF NOT EXISTS idx_malaria_observation_zone_id ON malaria_observation (zone_id);
CREATE INDEX IF NOT EXISTS idx_malaria_observation_date_recorded ON malaria_observation (date_recorded);
CREATE INDEX IF NOT EXISTS idx_malaria_observation_zone_id_date_recorded ON malaria_observation (zone_id, date_recorded);
```

`spring.flyway.baseline-on-migrate=true` + `spring.flyway.baseline-version=0` let Flyway adopt the existing RDS schema on first run rather than trying to recreate it. The DB user needs `CREATE` privilege.

**Manual fallback**, if the app's DB role can't run DDL: run the SQL above directly against RDS with a privileged role. For a production-sized table where a brief write lock is unacceptable, use the non-locking variant instead (must run outside a transaction, so as a standalone `psql` session — not a plain Flyway migration):

```sql
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_malaria_observation_zone_id_date_recorded
    ON malaria_observation (zone_id, date_recorded);
```

### `@Transactional(readOnly = true)`

Added at the class level on `ZoneService` and `MalariaObservationService`.

### Logging

SLF4J logging added to `GlobalExceptionHandler` (WARN/ERROR) and to the not-found paths in both services.

### CORS

`config/WebConfig.java` (`WebMvcConfigurer`, not `@EnableWebMvc` — that would have disabled static-resource serving) allows `GET` on `/api/**` from origins in `eoha.cors.allowed-origins`, defaulting to `http://localhost:3000` and overridable via the `CORS_ALLOWED_ORIGINS` environment variable.

### Status dashboard

A minimalist, Railway-styled status page is now served at `/` (`src/main/resources/static/`): live health pill, version/uptime/CORS metadata (via the new `GET /api/info`), and a clickable reference of every current and deprecated endpoint. `HealthController`'s root mapping was removed so Spring Boot's static `index.html` wins at `/`; `GET /api/health` is unchanged.

### Tests removed

All existing tests (`src/test/java/**`) and the `*-test` Spring Boot starter dependencies were removed, along with the now-stale `TEST_REPORT.md` and `Tests.md`. There is currently no automated test suite — see "Deferred / Future" below.

---

## Deferred / Future

- **Reintroduce a test suite** — unit tests for the services (Mockito) and slice tests for the controllers (`@WebMvcTest`) against the new DTO-based contracts, plus a couple of `ResourceNotFoundException → 404` vs. generic `RuntimeException → 500` cases.
- **Trim large fields from list responses** — e.g. omit `boundaryPolygon` from `ZoneResponse` when returned as part of a list, keep it only on the single-zone lookup.
- **`Specification`-based dynamic filtering** if more optional filters are added later; the current null-guarded `@Query` approach doesn't scale much past two or three filters per resource.
- **Remove the deprecated `/api/zones/**` and `/api/malaria-observations/**` aliases** once consumers have migrated to `/api/v1`.
- **`CREATE INDEX CONCURRENTLY`** for the production RDS instance once the table sizes make a locking `CREATE INDEX` risky (see manual fallback above).
- **Auth / rate limiting** — the API has no authentication or throttling; add if it's ever exposed beyond trusted frontends.
