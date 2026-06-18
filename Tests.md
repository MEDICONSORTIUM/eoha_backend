# EOHA Backend - Tests

This document describes the test suite for the EOHA (Environmental and Observational Health Analytics) backend, covering what each test class verifies and how to run the tests.

**Run all new tests**: `./mvnw test -Dtest='ZoneServiceTest,MalariaObservationServiceTest,ZoneControllerTest,MalariaObservationControllerTest,HealthControllerTest'`

**Run all tests** (requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` env vars for the full-context smoke test): `./mvnw test`

**Framework**: JUnit Jupiter 6.0.3 · Mockito 5.23.0 · AssertJ 3.27.7 · Spring Boot 4.1.0

---

## 1. ZoneServiceTest
**Type**: Unit test — no Spring context loaded
**Layer tested**: Service (`ZoneService`)
**Dependencies mocked**: `ZoneRepository`
**Location**: `src/test/java/com/eoha/eoha_backend/service/ZoneServiceTest.java`
**Description**: Verifies that `ZoneService` correctly delegates to `ZoneRepository` and applies the expected business rules, including throwing a `RuntimeException` when a zone is not found.
**Tests**:
- `getAllZones_returnsAll` — `findAll()` result is returned as-is.
- `getZoneById_found` — a present `Optional` returns the matching `Zone`.
- `getZoneById_notFound_throwsRuntimeException` — an empty `Optional` causes `RuntimeException("Zone not found")`.
- `getZonesByProvince_returnsFilteredList` — delegates to `findByProvinceName()` and returns its result.
- `getZonesByMunicipality_returnsFilteredList` — delegates to `findByMunicipalityName()` and returns its result.

---

## 2. MalariaObservationServiceTest
**Type**: Unit test — no Spring context loaded
**Layer tested**: Service (`MalariaObservationService`)
**Dependencies mocked**: `MalariaObservationRepository`
**Location**: `src/test/java/com/eoha/eoha_backend/service/MalariaObservationServiceTest.java`
**Description**: Verifies that `MalariaObservationService` correctly delegates all query methods to `MalariaObservationRepository` and throws a `RuntimeException` when an observation record is not found.
**Tests**:
- `getAllObservations_returnsAll` — `findAll()` result is returned as-is.
- `getObservationById_found` — a present `Optional` returns the matching `MalariaObservation`.
- `getObservationById_notFound_throwsRuntimeException` — an empty `Optional` causes `RuntimeException("Malaria observation not found")`.
- `getObservationsByZone_returnsFilteredList` — delegates to `findByZoneId()`.
- `getObservationsByDate_returnsFilteredList` — delegates to `findByDateRecorded()`.
- `getObservationsByZoneAndDate_returnsFilteredList` — delegates to `findByZoneIdAndDateRecorded()`.

---

## 3. ZoneControllerTest
**Type**: Web slice test — only the Web MVC layer is loaded
**Layer tested**: Controller (`ZoneController`)
**Dependencies mocked**: `ZoneService`
**Location**: `src/test/java/com/eoha/eoha_backend/controller/ZoneControllerTest.java`
**Description**: Verifies the HTTP contract of the `/api/zones` endpoints: correct status codes, the `ApiResponse` JSON envelope (`success`, `data`), and that an unhandled `RuntimeException` propagates when the service cannot find a zone (no global error handler is currently registered).
**Tests**:
- `getAllZones_returns200` — `GET /api/zones` → 200, `$.success == true`, `$.data` is a JSON array.
- `getZoneById_found_returns200` — `GET /api/zones/1` → 200, `$.success == true`.
- `getZoneById_notFound_propagatesException` — `GET /api/zones/99` → `RuntimeException` propagates with message `"Zone not found"`.
- `getZonesByProvince_returns200` — `GET /api/zones/province/Limpopo` → 200, `$.success == true`.
- `getZonesByMunicipality_returns200` — `GET /api/zones/municipality/Tzaneen` → 200, `$.success == true`.

---

## 4. MalariaObservationControllerTest
**Type**: Web slice test — only the Web MVC layer is loaded
**Layer tested**: Controller (`MalariaObservationController`)
**Dependencies mocked**: `MalariaObservationService`
**Location**: `src/test/java/com/eoha/eoha_backend/controller/MalariaObservationControllerTest.java`
**Description**: Verifies the HTTP contract of the `/api/malaria-observations` endpoints, including ISO-8601 date parsing (`yyyy-MM-dd`) in path variables and the standard `ApiResponse` envelope on all successful responses.
**Tests**:
- `getAllObservations_returns200` — `GET /api/malaria-observations` → 200, `$.success == true`, `$.data` is a JSON array.
- `getObservationById_found_returns200` — `GET /api/malaria-observations/1` → 200, `$.success == true`.
- `getObservationById_notFound_propagatesException` — `GET /api/malaria-observations/99` → `RuntimeException` propagates with message `"Malaria observation not found"`.
- `getObservationsByZone_returns200` — `GET /api/malaria-observations/zone/1` → 200, `$.success == true`.
- `getObservationsByDate_returns200` — `GET /api/malaria-observations/date/2024-01-15` → 200; confirms `@DateTimeFormat(iso = ISO.DATE)` parses the path variable correctly.
- `getObservationsByZoneAndDate_returns200` — `GET /api/malaria-observations/zone/1/date/2024-01-15` → 200; confirms combined zone + date path variable parsing.

---

## 5. HealthControllerTest
**Type**: Web slice test — only the Web MVC layer is loaded
**Layer tested**: Controller (`HealthController`)
**Dependencies mocked**: None
**Location**: `src/test/java/com/eoha/eoha_backend/controller/HealthControllerTest.java`
**Description**: Verifies the two health-check endpoints return the expected plain-text responses. These endpoints are used by the Data Reliability / Infrastructure Agent to confirm the API is reachable.
**Tests**:
- `rootEndpoint_returns200` — `GET /` → 200, body = `"Spring Boot API is running"`.
- `healthEndpoint_returns200` — `GET /api/health` → 200, body = `"OK"`.
