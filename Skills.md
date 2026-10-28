# EOHA Backend - Agent Skills

This document outlines the skills (tools) available to the AI Agents interacting with the EOHA backend. These skills directly leverage the exposed REST APIs.

All API responses are wrapped in an `ApiResponse<T>` envelope:
```json
{ "success": boolean, "data": <T>, "message": string | null }
```

---

## 1. FetchZoneDataSkill
**Description**: Allows an agent to retrieve geographical and demographic data about specific zones, municipalities, or provinces.
**API Endpoints Utilized**:
- `GET /api/zones` — Fetch all zones.
- `GET /api/zones/{id}` — Fetch details for a specific zone by ID.
- `GET /api/zones/province/{provinceName}` — Fetch all zones within a given province.
- `GET /api/zones/municipality/{municipalityName}` — Fetch all zones within a given municipality.
**Inputs**: `zoneId` (optional, Integer), `provinceName` (optional, String), `municipalityName` (optional, String)
**Outputs**: `ApiResponse<List<Zone>>` or `ApiResponse<Zone>` — each `Zone` contains:
- `zoneId`, `parentZoneId`, `zoneName`, `zoneType`, `externalZoneId`
- `provinceName`, `municipalityName`
- `latitude`, `longitude`, `boundaryPolygon`
- `basePopulation`, `populationDensityPerKm2`, `dominantHabitatCode`

---

## 2. FetchObservationDataSkill
**Description**: Allows an agent to retrieve environmental observation records that are crucial for assessing malaria risk.
**API Endpoints Utilized**:
- `GET /api/malaria-observations` — Fetch all recorded observations.
- `GET /api/malaria-observations/{id}` — Fetch a specific observation record.
- `GET /api/malaria-observations/zone/{zoneId}` — Fetch all observations for a specific geographic zone.
- `GET /api/malaria-observations/date/{dateRecorded}` — Fetch observations across all zones for a specific date.
- `GET /api/malaria-observations/zone/{zoneId}/date/{dateRecorded}` — Fetch observations for a specific zone on a specific date.
**Inputs**: `zoneId` (optional, Integer), `dateRecorded` (optional, ISO-8601 String: `yyyy-MM-dd`)
**Note**: `dateRecorded` path variables must be formatted as ISO-8601 dates (e.g., `2024-01-15`), as enforced by `@DateTimeFormat(iso = ISO.DATE)` in the controller.
**Outputs**: `ApiResponse<List<MalariaObservation>>` or `ApiResponse<MalariaObservation>` — each `MalariaObservation` contains:
- `envRecordId`, `zoneId`, `dateRecorded`
- `lstSurfaceTempC` — land surface temperature (°C)
- `airTempC` — air temperature (°C)
- `soilMoisturePct` — soil moisture percentage
- `ndviVegetationIndex` — Normalized Difference Vegetation Index
- `ndwiWaterIndex` — Normalized Difference Water Index
- `agricPercentage` — agricultural land coverage percentage

---

## 3. AnalyzeEnvironmentalRiskSkill (Internal/Compute Skill)
**Description**: A computational skill used by the Epidemiologist Agent. Processes raw data from `FetchObservationDataSkill` and applies epidemiological heuristics to calculate a Malaria Risk Score.
**Logic**:
- High `lstSurfaceTempC` / `airTempC` combined with high `soilMoisturePct` / `ndwiWaterIndex` → elevated mosquito breeding probability.
- High `ndviVegetationIndex` indicates dense vegetation providing mosquito habitat.
- Outputs a normalized risk score (0.0–1.0) per zone per date.

---

## 4. MapZoneHierarchySkill (Internal/Compute Skill)
**Description**: Helps the Geospatial Agent reconstruct parent-child relationships among zones using the `parentZoneId` field, grouped by `municipalityName` and `provinceName`.

---

## 5. GenerateRiskReportSkill (Internal/Compute Skill)
**Description**: Used by the Health Policy Agent to format raw data and computed risk scores into markdown or PDF reports suitable for human health officials.

---

## 6. HealthCheckSkill
**Description**: Used by the Infrastructure Agent to verify that the backend API is running and reachable.
**API Endpoints Utilized**:
- `GET /` — Returns plain text `"Spring Boot API is running"`.
- `GET /api/health` — Returns plain text `"OK"`.
**Inputs**: None
**Outputs**: Plain text string (not an `ApiResponse` envelope). A non-200 response or connection error indicates the service is down.
