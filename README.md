# EOHA Backend

Express 5 REST API for the EOHA frontend. It serves canned sample data in memory;
there is no database or live malaria prediction model.

## Run

Requires Node.js 22 or newer and npm. Java and Maven are no longer required.

```bash
npm ci
cp .env.example .env       # optional local overrides
npm start
```

Open http://localhost:8080 for the status dashboard. `npm run dev` enables file
watching. `npm test` runs the API contract tests. With nvm, use `nvm use` first.
The start/dev commands load `.env` if present; existing environment variables take
precedence. Never commit local `.env` files.

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| PORT | 8080 | Integer TCP port, 1–65535 |
| EOHA_NAME | EOHA Backend | Display name |
| EOHA_VERSION | 0.0.1-SNAPSHOT | Version metadata |
| CORS_ALLOWED_ORIGINS | http://localhost:[*],http://127.0.0.1:[*] | Comma-separated allowed origin patterns |

For deployment, set CORS_ALLOWED_ORIGINS to your frontend origin, install with
`npm ci --omit=dev`, and run `npm start` under your platform's process manager.
The server handles SIGINT/SIGTERM and drains connections with a 10-second limit.

## API contract

`GET /api/health` returns plain text `OK`, matching the original Spring probe.
All JSON endpoints use `{ "success": true, "data": ..., "message": null }`.
Failures use `{ "success": false, "data": null, "message": "..." }`.

| Method | Route | Parameters / behavior |
| --- | --- | --- |
| GET | /api/info | name, version, apiVersion, startedAt, corsAllowedOrigins |
| GET | /api/v1/zones | province, municipality, page, size |
| GET | /api/v1/zones/:id | Single zone; unknown ID returns 404 |
| GET | /api/v1/malaria-observations | zoneId, date, page, size |
| GET | /api/v1/malaria-observations/:id | Single observation; unknown ID returns 404 |

Zone filters are exact, case-insensitive matches. Date filters use valid
YYYY-MM-DD calendar dates. List data has content, page, size, totalElements,
totalPages, first, and last. Defaults are page=0 and size=20; empty page/size
values also use these defaults. Negative pages become 0; sizes below 1 become 20.
An out-of-range page returns empty content. Invalid integers/dates return 400.
Integers are restricted to signed 32-bit values, matching Java's binding.
Unknown API routes return an enveloped 404.

Zone records retain zoneId, parentZoneId, zoneName, boundaryPolygon,
basePopulation, dominantHabitatCode, externalZoneId, zoneType, latitude,
longitude, populationDensityPerKm2, municipalityName, and provinceName.
Observation records retain envRecordId, zoneId, dateRecorded, lstSurfaceTempC,
soilMoisturePct, ndviVegetationIndex, ndwiWaterIndex, airTempC, and agricPercentage.

CORS supports GET/HEAD/OPTIONS under /api, reflects requested preflight headers,
and permits localhost/127.0.0.1 development ports by default. Serve the frontend
using HTTP rather than file://.

```js
const response = await fetch('http://localhost:8080/api/v1/zones?province=Limpopo');
const { data } = await response.json();
```

## Structure

- src/server.js: process startup and shutdown
- src/app.js: application assembly, static dashboard and error handling
- src/routes/: HTTP endpoints
- src/services/: sample data and filtering
- src/lib/: response envelopes, validation, pagination and errors
- src/config.js and src/cors.js: configuration and browser access
- public/: status dashboard
- test/: API regression tests

See MIGRATION.md for the Spring-to-Express mapping and compatibility decisions.
When a data source is chosen, replace service internals while retaining the API.
