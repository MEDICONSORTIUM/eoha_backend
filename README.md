# EOHA Backend

Spring Boot REST API for the EOHA web application. The backend connects to an AWS RDS PostgreSQL database and exposes read endpoints for zone and malaria observation data.

## Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* AWS RDS
* Maven

## Project Structure

```text
src/main/java/com/example/eoha_backend
│
├── EohaBackendApplication.java
│
├── controller
│   ├── HealthController.java
│   ├── ZoneController.java
│   └── MalariaObservationController.java
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
└── dto
    └── ApiResponse.java
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

The frontend does not connect directly to the database. All database access is handled through the Spring Boot API.

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

## Local Setup

### 1. Clone the repository

```bash
git clone https://github.com/MEDICONSORTIUM/eoha_backend.git
cd eoha_backend
```

### 2. Set database environment variables

In PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://your-rds-endpoint:5432/your_database?sslmode=require"
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
```

Do not commit real database credentials to GitHub.

### 3. Check `application.properties`

The file should use environment variables:

```properties
spring.application.name=eoha_backend

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

`spring.jpa.hibernate.ddl-auto=none` is used because the database tables already exist.

### 4. Run the application

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The API should start on:

```text
http://localhost:8080
```

## API Endpoints

Base URL for local development:

```text
http://localhost:8080
```

## Health Endpoints

| Method | Route         | Description                      |
| ------ | ------------- | -------------------------------- |
| GET    | `/`           | Checks if the backend is running |
| GET    | `/api/health` | Basic health check endpoint      |

### Example Requests

```text
GET http://localhost:8080/
GET http://localhost:8080/api/health
```

## Zone Endpoints

| Method | Route                                        | Description                    |
| ------ | -------------------------------------------- | ------------------------------ |
| GET    | `/api/zones`                                 | Get all zones                  |
| GET    | `/api/zones/{id}`                            | Get a single zone by `zone_id` |
| GET    | `/api/zones/province/{provinceName}`         | Get zones by province name     |
| GET    | `/api/zones/municipality/{municipalityName}` | Get zones by municipality name |

### Example Requests

```text
GET http://localhost:8080/api/zones
GET http://localhost:8080/api/zones/59500024
GET http://localhost:8080/api/zones/province/KwaZulu-Natal
GET http://localhost:8080/api/zones/municipality/Ethekwini%20Metropolitan%20Municipality
```

## Malaria Observation Endpoints

| Method | Route                                                         | Description                                                     |
| ------ | ------------------------------------------------------------- | --------------------------------------------------------------- |
| GET    | `/api/malaria-observations`                                   | Get all malaria environmental observations                      |
| GET    | `/api/malaria-observations/{id}`                              | Get one malaria observation by `env_record_id`                  |
| GET    | `/api/malaria-observations/zone/{zoneId}`                     | Get malaria observations for a specific zone                    |
| GET    | `/api/malaria-observations/date/{dateRecorded}`               | Get malaria observations for a specific date                    |
| GET    | `/api/malaria-observations/zone/{zoneId}/date/{dateRecorded}` | Get malaria observations for a specific zone on a specific date |

### Example Requests

```text
GET http://localhost:8080/api/malaria-observations
GET http://localhost:8080/api/malaria-observations/8054
GET http://localhost:8080/api/malaria-observations/zone/41603005
GET http://localhost:8080/api/malaria-observations/date/2026-03-01
GET http://localhost:8080/api/malaria-observations/zone/41603005/date/2026-03-01
```

## Response Format

Most API responses follow this structure:

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

For list endpoints, `data` will be an array:

```json
{
  "success": true,
  "data": [],
  "message": null
}
```

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
* The API uses existing PostgreSQL tables and does not create or update the database schema automatically.
* The frontend should call the Spring Boot API, not the AWS RDS database directly.
