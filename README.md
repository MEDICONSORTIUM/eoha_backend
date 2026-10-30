# EOHA Backend

Spring Boot REST API for the Earth Observation Health Analytics platform.

This version targets the normalized PostgreSQL schema used by the current EOHA data pipeline. The previous `zone` and `malaria_observation` JPA mappings have been removed.

## Stack

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring JDBC
- PostgreSQL
- AWS RDS
- Maven

## Database schema

The API reads from schema `eoha` and exposes these tables:

- `regions`
- `environmental_observations`
- `environmental_variables`
- `data_sources`
- `etl_runs`

The API is read-only. SQL table names are not accepted from clients; only the fixed whitelist above is queryable.

## Configuration

Set the database connection through environment variables.

PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://YOUR_RDS_ENDPOINT:5432/eoha?sslmode=require"
$env:DB_USERNAME="YOUR_DB_USER"
$env:DB_PASSWORD="YOUR_DB_PASSWORD"
```

Optional:

```powershell
$env:DB_POOL_SIZE="10"
$env:PORT="8080"
```

No database credentials should be committed to GitHub. Local settings may be
saved in the Git-ignored `.env.local` file. It is loaded automatically as properties
and accepts `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, and
`DB_SSLMODE`; `DB_URL` and `DB_USERNAME` remain supported as overrides.

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

The service runs on `http://localhost:8080` by default.

## Health endpoints

| Method | Route | Purpose |
|---|---|---|
| GET | `/` | Confirms the HTTP service is running |
| GET | `/api/health` | Application liveness |
| GET | `/api/health/database` | Tests PostgreSQL connectivity and verifies all five required EOHA tables |

`/api/health/database` returns HTTP `200` only when the database is reachable and all five required tables are present. It returns `503` otherwise.

## Data endpoints

The five raw-table endpoints support `limit` and `offset`. `/api/schema` and
`/api/malaria-risk-inputs` return arrays and do not use pagination parameters.

| Method | Route |
|---|---|
| GET | `/api/regions` |
| GET | `/api/environmental-observations` |
| GET | `/api/environmental-variables` |
| GET | `/api/data-sources` |
| GET | `/api/etl-runs` |
| GET | `/api/schema` |
| GET | `/api/malaria-risk-inputs` |

Example:

```text
GET http://localhost:8080/api/environmental-observations?limit=100&offset=0
```

The maximum page size is 500.

## Response format

```json
{
  "success": true,
  "data": {
    "items": [],
    "total": 0,
    "limit": 100,
    "offset": 0
  },
  "message": null
}
```

The data layer uses JDBC rather than JPA entities. This is intentional: the normalized environmental schema contains flexible PostgreSQL columns and metadata, and the API should return the database's actual current row shape rather than maintaining duplicated Java entity definitions for every schema field.

## First connectivity test

After setting the environment variables and starting the backend:

```powershell
Invoke-RestMethod http://localhost:8080/api/health/database
```

Then verify existing seed data:

```powershell
Invoke-RestMethod http://localhost:8080/api/data-sources
```

Finally inspect the database columns visible to the backend:

```powershell
Invoke-RestMethod http://localhost:8080/api/schema
```

## Malaria scoring inputs

`GET /api/malaria-risk-inputs` returns `{success:true,data:[...],message:null}`.
It joins monthly ward measurements and observation metadata into one row per
ward/month, with municipality, province, coordinates, and the inputs needed by
the prototype risk formula. SQL dates are returned as `YYYY-MM-DD` strings to
preserve calendar months across timezones. Results are cached for 60 seconds;
failed queries are not cached. Existing raw-table endpoints remain available.

The endpoint accepts only mean ward observations. Duplicate required variable
measurements and unsupported units yield null inputs, which the frontend excludes
from scoring. Soil moisture remains in m³/m³, temperature in °C, and NDWI in index
units. WorldPop mean counts per 100 m pixel are multiplied by 100 to approximate
persons/km², matching the existing EOHA pipeline convention. This conversion is
restricted to the WORLDPOP 100 m source; it must not be applied to regional totals.
Agriculture fractions are multiplied by 100 for percentage display.

This endpoint reads database data and does not run ETL or write database records.

### Returned scoring fields

Each array item contains `region_id`, `region_code`, `region_name`, `province`,
`municipality`, `latitude`, `longitude`, `observation_month`, `lst_c`,
`soil_moisture`, `ndwi`, `population_density_per_km2`, `air_temp_c` and
`agric_percentage`. The controller wraps the array directly under `data`, not
`data.items`, so it matches the prototype's existing JavaScript consumer.

`observation_month` is calendar-date text, not a timezone-shifted timestamp.
Coordinates and municipality come from observation JSON metadata; ward/province
names come from regions. The query selects mean observations for ward regions.
Missing, duplicate or incompatible required measurements remain null, and the
frontend excludes incomplete records instead of assigning an artificial low score.
The backend returns measurements; the existing risk formula runs in the frontend.

### Deploy the route to Render

The endpoint source files must be committed to the branch that Render builds.
Having them only in an untracked local workspace does not put them into a cloud
build. Merge the scoring-input PR into the configured deployment branch (the PR
targets `main`), then redeploy that commit on Render. If the service still tracks
another branch, ensure that branch contains the merged change or select `main`.
The Docker build runs Maven verification and packages the controller/service into
the executable JAR. No database migration, extraction job or table writes are needed.

Verify both readiness and the route after deployment:

```bash
curl https://eoha-backend.onrender.com/api/health/database
curl https://eoha-backend.onrender.com/api/malaria-risk-inputs
```

The second response must be HTTP 200 with `success: true` and an array in `data`.
A 404 means the running artifact/branch still lacks the route; database read
failures return 503 through the existing error handler. The first uncached query
reads all ward-month inputs and can take several seconds; successful results are
cached for 60 seconds. Failed queries are not cached.

The prototype's Node.js proxy already defaults to the Render origin, so no frontend
scoring-code change is needed once this backend route is deployed. Browser-direct
cross-origin access still requires a backend CORS policy; the same-origin proxy
does not require changing CORS for this endpoint.

### Verification

On 30 October 2026, Maven `verify` passed five tests, including the HTTP success
contract, calendar dates, null measurement preservation, database error handling,
and cache recovery. The packaged JAR contained both new classes and was started
against the existing RDS database on an isolated local port. Its endpoint returned
44,680 ward-month input records; the frontend normalizer accepted 44,480 complete
scoring records and excluded 200 incomplete records across January–October 2026.
An uncached response took about 11 seconds and a cached response about 0.4 seconds
in that check. These are local packaged-application results; the hosted route still
requires deployment of this change.
