# EOHA Backend

Spring Boot REST API for the EOHA web application. The backend connects to an AWS RDS PostgreSQL database and exposes read endpoints for zone and malaria observation data.

A live status dashboard is served at the API root — see [Status Dashboard](#status-dashboard).

## Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Flyway (schema migrations / indexes)
* PostgreSQL
* AWS RDS
* Maven

## Project Structure

```text
src/main/java/com/eoha/eoha_backend
│
├── EohaBackendApplication.java
│
├── config
│   └── WebConfig.java              # CORS
│
├── controller
│   ├── HealthController.java
│   ├── InfoController.java         # dashboard metadata
│   ├── ZoneController.java         # deprecated /api/zones/**
│   ├── ZoneV1Controller.java       # /api/v1/zones
│   ├── MalariaObservationController.java      # deprecated /api/malaria-observations/**
│   └── MalariaObservationV1Controller.java    # /api/v1/malaria-observations
│
├── service
│   ├── ZoneService.java
│   └── MalariaObservationService.java
│
├── repository
│   ├── ZoneRepository.java
│   └── MalariaObservationRepository.java
│
├── model
│   ├── Zone.java
│   └── MalariaObservation.java
│
├── dto
│   ├── ApiResponse.java
│   ├── PagedResponse.java
│   ├── InfoResponse.java
│   ├── ZoneResponse.java
│   └── MalariaObservationResponse.java
│
└── exception
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java

src/main/resources
├── application.properties
├── db/migration/                   # Flyway SQL
│   └── V1__add_search_indexes.sql
└── static/                         # status dashboard (served at /)
    ├── index.html
    ├── styles.css
    └── app.js
```

## Architecture

```text
Frontend Web App
      ↓
Spring Boot REST API
      ↓
Spring Data JPA
      ↓
AWS RDS PostgreSQL
```

The frontend does not connect directly to the database. All database access is handled through the Spring Boot API. Controllers never return JPA entities — every response is mapped to a dedicated DTO (`ZoneResponse`, `MalariaObservationResponse`), so schema changes don't automatically change the API contract.

## Database Tables

The current backend works with the following PostgreSQL tables:

### `zone`

Stores geographic zone information.

Important fields include:

```text
zone_id
parent_zone_id
zone_name
boundary_polygon
base_population
dominant_habitat_code
external_zone_id
zone_type
latitude
longitude
population_density_per_km2
municipality_name
province_name
```

### `malaria_observation`

Stores environmental malaria observation data per zone and date.

Important fields include:

```text
env_record_id
zone_id
date_recorded
lst_surface_temp_c
soil_moisture_pct
ndvi_vegetation_index
ndwi_water_index
air_temp_c
agric_percentage
```

The `malaria_observation.zone_id` field references `zone.zone_id`.

## Database Migrations (Flyway)

Schema changes (currently: indexes) are managed by [Flyway](https://flywaydb.org/), scripts live in `src/main/resources/db/migration/`. Hibernate itself never touches the schema (`spring.jpa.hibernate.ddl-auto=none`) — the tables already exist in RDS.

On first run against an existing database, Flyway **baselines** the current schema (`spring.flyway.baseline-on-migrate=true`) and then applies any migration newer than the baseline. The database user needs `CREATE` privilege to add indexes.

`V1__add_search_indexes.sql` adds indexes on the columns the API filters on: `zone.province_name`, `zone.municipality_name`, `malaria_observation.zone_id`, `malaria_observation.date_recorded`, and the composite `(zone_id, date_recorded)`.

If you cannot grant the app's DB user schema privileges, apply the same indexes manually — see [IMPROVEMENTS.md](IMPROVEMENTS.md) for the raw SQL.

## CORS

Browser-based frontends (React, Angular, etc.) on a different origin are allowed to call `GET` endpoints under `/api/**`. Allowed origins come from `eoha.cors.allowed-origins` (`application.properties`, comma-separated), overridable via the `CORS_ALLOWED_ORIGINS` environment variable. Default: `http://localhost:3000`.

## Running the Backend

Requires JDK 21. Use the bundled wrapper (`./mvnw`, or `.\mvnw.cmd` on Windows) — no separate Maven install needed. The app always needs a reachable PostgreSQL at startup (JPA detects the dialect, Flyway runs migrations); it will not boot without one.

### Database connection

The app resolves its datasource in this order:

1. A `.env` file at the project root, if present — loaded via `spring.config.import`. It is a plain properties file and its keys win:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=eoha
   spring.datasource.password=your_password
   ```

   `.env` is git-ignored — keep real credentials only here, never in `application.properties`.

2. Otherwise the `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` environment variables (how CI / deployments supply it):

   ```bash
   export DB_URL="jdbc:postgresql://your-rds-endpoint:5432/your_database?sslmode=require"
   export DB_USERNAME="your_username"
   export DB_PASSWORD="your_password"
   ```

### Start

```bash
git clone https://github.com/MEDICONSORTIUM/eoha_backend.git
cd eoha_backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080` (dashboard at `/`). On WSL, `localhost` is forwarded to Windows, so a Windows browser reaches it directly. Stop with `Ctrl+C`, or from another shell: `fuser -k 8080/tcp`.

On first connection to a given database Flyway baselines the existing schema and applies `V1__add_search_indexes.sql`; watch the startup log for a `Migrating schema "public" to version "1"` line.

### Local PostgreSQL (no RDS access)

To run fully offline against a throwaway local database:

```bash
sudo apt-get install -y postgresql
sudo service postgresql start          # WSL: re-run after each restart
sudo -u postgres psql <<'SQL'
CREATE ROLE eoha LOGIN SUPERUSER PASSWORD 'choose_a_password';
SQL
```

Point `.env` line 1 at `jdbc:postgresql://localhost:5432/postgres`, create the two tables to match the entities (`zone`, `malaria_observation` — columns as listed under [Database Tables](#database-tables)), then `./mvnw spring-boot:run`. Flyway adds the indexes on top.

## Status Dashboard

Visit `http://localhost:8080/` for a live status page: service health, build/version info, CORS configuration, uptime, and a clickable reference of every endpoint (current and deprecated). It's backed by `GET /api/health` and `GET /api/info`.

## API Endpoints

Base URL for local development:

```text
http://localhost:8080
```

## API Versioning

Resource endpoints are versioned under `/api/v1/`. The pre-versioning path-variable routes (`/api/zones/province/{name}`, `/api/malaria-observations/zone/{id}`, etc.) are kept as **deprecated aliases** for backward compatibility and will be removed in a future major version — new integrations should use `/api/v1/...`.

## Health Endpoints

| Method | Route         | Description                      |
| ------ | ------------- | -------------------------------- |
| GET    | `/`           | Status dashboard (HTML)          |
| GET    | `/api/health` | Basic health check endpoint      |
| GET    | `/api/info`   | Build/version metadata, CORS config |

### Example Requests

```text
GET http://localhost:8080/api/health
GET http://localhost:8080/api/info
```

## Zone Endpoints (`/api/v1`)

| Method | Route                  | Description                                                        |
| ------ | ---------------------- | ------------------------------------------------------------------ |
| GET    | `/api/v1/zones`        | List zones. Optional `?province=`, `?municipality=`, `?page=`, `?size=` |
| GET    | `/api/v1/zones/{id}`   | Get a single zone by `zone_id`                                     |

### Example Requests

```text
GET http://localhost:8080/api/v1/zones
GET http://localhost:8080/api/v1/zones?province=KwaZulu-Natal
GET http://localhost:8080/api/v1/zones?province=KwaZulu-Natal&municipality=Ethekwini%20Metropolitan%20Municipality
GET http://localhost:8080/api/v1/zones?page=0&size=20
GET http://localhost:8080/api/v1/zones/59500024
```

## Malaria Observation Endpoints (`/api/v1`)

| Method | Route                                | Description                                                     |
| ------ | ------------------------------------ | ----------------------------------------------------------------|
| GET    | `/api/v1/malaria-observations`       | List observations. Optional `?zoneId=`, `?date=`, `?page=`, `?size=` |
| GET    | `/api/v1/malaria-observations/{id}`  | Get one observation by `env_record_id`                          |

### Example Requests

```text
GET http://localhost:8080/api/v1/malaria-observations
GET http://localhost:8080/api/v1/malaria-observations?zoneId=41603005
GET http://localhost:8080/api/v1/malaria-observations?date=2026-03-01
GET http://localhost:8080/api/v1/malaria-observations?zoneId=41603005&date=2026-03-01
GET http://localhost:8080/api/v1/malaria-observations/8054
```

## Deprecated Endpoints

Retained for backward compatibility; prefer the `/api/v1` routes above.

| Method | Route                                                         |
| ------ | -------------------------------------------------------------- |
| GET    | `/api/zones`                                                    |
| GET    | `/api/zones/{id}`                                               |
| GET    | `/api/zones/province/{provinceName}`                            |
| GET    | `/api/zones/municipality/{municipalityName}`                    |
| GET    | `/api/malaria-observations`                                     |
| GET    | `/api/malaria-observations/{id}`                                |
| GET    | `/api/malaria-observations/zone/{zoneId}`                       |
| GET    | `/api/malaria-observations/date/{dateRecorded}`                 |
| GET    | `/api/malaria-observations/zone/{zoneId}/date/{dateRecorded}`   |

## Response Format

Every endpoint responds with the same envelope:

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

For list endpoints without pagination (the deprecated routes), `data` is an array. For the `/api/v1` list endpoints, `data` is a page:

```json
{
  "success": true,
  "data": {
    "content": [ /* ZoneResponse[] or MalariaObservationResponse[] */ ],
    "page": 0,
    "size": 20,
    "totalElements": 134,
    "totalPages": 7,
    "first": true,
    "last": false
  },
  "message": null
}
```

## Error Behavior

A request for an entity that doesn't exist returns HTTP 404 with `success: false`:

```json
{
  "success": false,
  "data": null,
  "message": "Zone 999999 not found"
}
```

An invalid parameter (e.g. a non-numeric `id`) returns HTTP 400. Any other unexpected server error returns HTTP 500 with a generic message — details are logged server-side, not exposed to the client.

## Example Zone Response

```json
{
  "success": true,
  "data": {
    "zoneId": 59500024,
    "parentZoneId": null,
    "zoneName": "ETH_24",
    "boundaryPolygon": null,
    "basePopulation": 12,
    "dominantHabitatCode": "10.0",
    "externalZoneId": "59500024",
    "zoneType": null,
    "latitude": -29.85264,
    "longitude": 30.924128,
    "populationDensityPerKm2": 1279.1251,
    "municipalityName": "Ethekwini Metropolitan Municipality",
    "provinceName": "KwaZulu-Natal"
  },
  "message": null
}
```

## Example Malaria Observation Response

```json
{
  "success": true,
  "data": {
    "envRecordId": 8054,
    "zoneId": 41603005,
    "dateRecorded": "2026-03-01",
    "lstSurfaceTempC": 21.83502,
    "soilMoisturePct": 0.3396694,
    "ndviVegetationIndex": 0.5959259,
    "ndwiWaterIndex": -0.38863385,
    "airTempC": 17.569027,
    "agricPercentage": 0.021417777
  },
  "message": null
}
```

## Notes

* This backend is currently read-only for the main data endpoints.
* Database credentials must be stored using environment variables.
* The API uses existing PostgreSQL tables; Flyway only manages incremental schema additions (e.g. indexes), not table creation.
* The frontend should call the Spring Boot API, not the AWS RDS database directly.
* There is currently no automated test suite (see [IMPROVEMENTS.md](IMPROVEMENTS.md) for the plan to reintroduce one).
