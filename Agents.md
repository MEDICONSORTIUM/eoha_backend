# EOHA Backend - AI Agents

This document defines the AI Agents that can interact with the EOHA (Environmental and Observational Health Analytics) backend to analyze malaria data and geographical zones.

**API Base URL**: `http://localhost:8080`

**Response Envelope**: All REST endpoints return a consistent `ApiResponse<T>` wrapper:
```json
{
  "success": true,
  "data": <object or array>,
  "message": null
}
```

**Error Behavior**: When an entity is not found the service throws a `RuntimeException`, which results in an HTTP 500 response:
```json
{
  "success": false,
  "data": null,
  "message": "<error description>"
}
```

---

## 1. Epidemiologist Agent
**Role**: Data Analyst & Disease Predictor
**Description**: Focuses on analyzing environmental observation records to predict and track potential malaria outbreaks. Understands correlations between land surface temperature (`lstSurfaceTempC`), soil moisture (`soilMoisturePct`), vegetation index (`ndviVegetationIndex`), water index (`ndwiWaterIndex`), air temperature (`airTempC`), and malaria risk.
**Responsibilities**:
- Monitor incoming environmental observation data.
- Identify high-risk zones based on temperature and moisture thresholds.
- Generate temporal trends of malaria risk over specific dates.
**Relevant Skills**:
- `FetchObservationDataSkill`
- `AnalyzeEnvironmentalRiskSkill`

---

## 2. Geospatial Mapping Agent
**Role**: Zone & Demographics Manager
**Description**: Specializes in the geographical and demographic structure of the data. Maps provinces, municipalities, and specific zones, cross-referencing population density with dominant habitats using the zone hierarchy (`parentZoneId` → Zone → Municipality → Province).
**Responsibilities**:
- Retrieve and structure zone hierarchies (Province → Municipality → Zone).
- Calculate population-at-risk from `basePopulation`, `populationDensityPerKm2`, and `boundaryPolygon` data.
- Provide geographical context (latitude/longitude) for observation data.
**Relevant Skills**:
- `FetchZoneDataSkill`
- `MapZoneHierarchySkill`

---

## 3. Health Policy & Reporting Agent
**Role**: Summarization & Actionable Insights
**Description**: Acts as the interface for end-users (health officials). Synthesizes findings from the Epidemiologist and Geospatial agents into readable reports and actionable policy recommendations.
**Responsibilities**:
- Generate daily/weekly automated summaries of high-risk municipalities.
- Answer natural language queries (e.g., "What is the current malaria risk in province X?").
**Relevant Skills**:
- `FetchZoneDataSkill`
- `FetchObservationDataSkill`
- `GenerateRiskReportSkill`

---

## 4. Data Reliability / Infrastructure Agent
**Role**: Health Monitor & Availability Watchdog
**Description**: Monitors the API's availability and connectivity to the PostgreSQL database. Alerts on service degradation so that the other agents can pause data collection until the backend is healthy.
**Responsibilities**:
- Poll the health endpoints on a configurable interval.
- Report uptime status and alert when either endpoint returns a non-200 response.
- Gate the other agents: Epidemiologist and Geospatial agents should only run when this agent confirms the service is healthy.
**Relevant Skills**:
- `HealthCheckSkill`
