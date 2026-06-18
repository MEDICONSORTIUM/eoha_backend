# EOHA Backend — Test Report

**Date**: 2026-06-18  
**Build tool**: Maven (./mvnw)  
**Test framework**: JUnit Jupiter 6.0.3, Mockito 5.23.0, AssertJ 3.27.7  
**Spring Boot**: 4.1.0 | **Spring Framework**: 7.0.8  

---

## Summary

| Metric | Value |
|--------|-------|
| Total tests run | 24 |
| Passed | **24** |
| Failed | 0 |
| Errors | 0 |
| Skipped | 0 |
| Build result | **SUCCESS** |

---

## Per-class Breakdown

| Test class | Tests | Result | Notes |
|-----------|-------|--------|-------|
| `ZoneServiceTest` | 5 | PASS | Pure unit tests, no Spring context |
| `MalariaObservationServiceTest` | 6 | PASS | Pure unit tests, no Spring context |
| `ZoneControllerTest` | 5 | PASS | `@WebMvcTest` slice, MockMvc |
| `MalariaObservationControllerTest` | 6 | PASS | `@WebMvcTest` slice, MockMvc |
| `HealthControllerTest` | 2 | PASS | `@WebMvcTest` slice, plain-text assertions |

---

## Test Coverage Detail

### ZoneServiceTest (5 tests)
- `getAllZones_returnsAll` — verifies `findAll()` delegation and full list is returned
- `getZoneById_found` — verifies `findById()` with present `Optional` returns the entity
- `getZoneById_notFound_throwsRuntimeException` — verifies empty `Optional` causes `RuntimeException("Zone not found")`
- `getZonesByProvince_returnsFilteredList` — verifies province filter delegation
- `getZonesByMunicipality_returnsFilteredList` — verifies municipality filter delegation

### MalariaObservationServiceTest (6 tests)
- `getAllObservations_returnsAll` — verifies `findAll()` delegation
- `getObservationById_found` — verifies `findById()` happy path
- `getObservationById_notFound_throwsRuntimeException` — verifies `RuntimeException("Malaria observation not found")`
- `getObservationsByZone_returnsFilteredList` — verifies `findByZoneId()` delegation
- `getObservationsByDate_returnsFilteredList` — verifies `findByDateRecorded()` delegation
- `getObservationsByZoneAndDate_returnsFilteredList` — verifies `findByZoneIdAndDateRecorded()` delegation

### ZoneControllerTest (5 tests)
- `getAllZones_returns200` — `GET /api/zones` → 200, `$.success == true`, `$.data` is array
- `getZoneById_found_returns200` — `GET /api/zones/1` → 200, `$.success == true`
- `getZoneById_notFound_propagatesException` — `GET /api/zones/99` → `RuntimeException` propagates (no global error handler in codebase)
- `getZonesByProvince_returns200` — `GET /api/zones/province/Limpopo` → 200
- `getZonesByMunicipality_returns200` — `GET /api/zones/municipality/Tzaneen` → 200

### MalariaObservationControllerTest (6 tests)
- `getAllObservations_returns200` — `GET /api/malaria-observations` → 200, `$.success == true`
- `getObservationById_found_returns200` — `GET /api/malaria-observations/1` → 200
- `getObservationById_notFound_propagatesException` — `GET /api/malaria-observations/99` → `RuntimeException` propagates
- `getObservationsByZone_returns200` — `GET /api/malaria-observations/zone/1` → 200
- `getObservationsByDate_returns200` — `GET /api/malaria-observations/date/2024-01-15` → 200 (ISO-8601 date parsing verified)
- `getObservationsByZoneAndDate_returns200` — `GET /api/malaria-observations/zone/1/date/2024-01-15` → 200

### HealthControllerTest (2 tests)
- `rootEndpoint_returns200` — `GET /` → 200, body = `"Spring Boot API is running"`
- `healthEndpoint_returns200` — `GET /api/health` → 200, body = `"OK"`

---

## Implementation Notes

### Spring Boot 4.x Compatibility Adjustments

Two changes were required compared to Spring Boot 3.x conventions:

1. **`@WebMvcTest` package moved**: In Spring Boot 4.x the annotation lives at  
   `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`  
   (was `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`).

2. **MockMvc exception propagation**: Spring Framework 7's MockMvc re-throws unhandled `RuntimeException` from `perform()` rather than converting it to a 500 response. The not-found tests assert via `assertThatThrownBy(() -> mockMvc.perform(...))` instead of `.andExpect(status().isInternalServerError())`.

---

## Coverage Gaps

| Gap | Recommendation |
|-----|----------------|
| No global exception handler | Add a `@ControllerAdvice` that maps `RuntimeException` → 404/500 with `ApiResponse.error(...)`. This would make error responses consistent and testable via status codes. |
| No integration tests | The existing `EohaBackendApplicationTests` (`@SpringBootTest`) requires real DB credentials. Consider adding an H2-backed integration test profile for CI. |
| No write-path coverage | All endpoints are read-only; if write endpoints are added, test them with `@DataJpaTest` against H2. |
| `EohaBackendApplicationTests` not executed | This test loads the full context and requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` environment variables to be set. It was excluded from this run. |
