# EOHA Backend

Placeholder REST API for the EOHA frontend. It returns **canned sample data** in the
final response shape so the UI can be built against it. No database — swap the hardcoded
lists in the service classes for a real data source when one is available.

## Run it

Requires JDK 21. Nothing else — no database, no environment variables.

```bash
git clone https://github.com/MEDICONSORTIUM/eoha_backend.git
cd eoha_backend
./mvnw spring-boot:run          # .\mvnw.cmd spring-boot:run on Windows
```

Starts on `http://localhost:8080`. A status page with the endpoint list is at `/`.
Stop with `Ctrl+C`.

## Endpoints

Every response is wrapped in the same envelope:

```json
{ "success": true, "data": <object | paged list>, "message": null }
```

| Method | Route | Notes |
| ------ | ----- | ----- |
| GET | `/api/health` | `"OK"` |
| GET | `/api/info` | name, version, uptime, CORS config |
| GET | `/api/v1/zones` | optional `?province=`, `?municipality=`, `?page=` (0), `?size=` (20) |
| GET | `/api/v1/zones/{id}` | one zone; unknown id → `404` |
| GET | `/api/v1/malaria-observations` | optional `?zoneId=`, `?date=` (ISO), `?page=`, `?size=` |
| GET | `/api/v1/malaria-observations/{id}` | one observation; unknown id → `404` |

### List response (`/api/v1/zones`)

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "zoneId": 1,
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
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "message": null
}
```

### Single observation (`/api/v1/malaria-observations/{id}`)

```json
{
  "success": true,
  "data": {
    "envRecordId": 1,
    "zoneId": 1,
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

### Errors

```json
{ "success": false, "data": null, "message": "Zone 999 not found" }   // 404 unknown id
{ "success": false, "data": null, "message": "Invalid value for parameter 'id'" }   // 400 bad param
```

## Calling it from a UI

CORS allows `GET`/`HEAD` on `/api/**` from any `localhost` / `127.0.0.1` port, so any
local dev server works with no config:

```js
const API = "http://localhost:8080";
const { data } = await (await fetch(`${API}/api/v1/zones?province=KwaZulu-Natal`)).json();
```

Serve the UI over http (e.g. `python -m http.server 5500`), not as a `file://` page.
For production set `CORS_ALLOWED_ORIGINS` to the real site origin(s).

## Structure

```text
src/main/java/com/eoha/eoha_backend
├── controller   HealthController, InfoController, ZoneV1Controller, MalariaObservationV1Controller
├── service      ZoneService, MalariaObservationService   ← hardcoded sample data lives here
├── dto          ApiResponse, PagedResponse, ZoneResponse, MalariaObservationResponse, InfoResponse
├── exception    GlobalExceptionHandler, ResourceNotFoundException
└── config       WebConfig (CORS)

src/main/resources/static   status dashboard served at /
```

## Making it real

Replace the `List.of(...)` placeholder data in `ZoneService` / `MalariaObservationService`
with calls to whatever the data source ends up being (JPA repository, HTTP client, etc.).
The controllers, DTOs, and error handling stay as they are.
