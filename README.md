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

No database credentials should be committed to GitHub.

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

All data endpoints support `limit` and `offset`.

| Method | Route |
|---|---|
| GET | `/api/regions` |
| GET | `/api/environmental-observations` |
| GET | `/api/environmental-variables` |
| GET | `/api/data-sources` |
| GET | `/api/etl-runs` |
| GET | `/api/schema` |

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
