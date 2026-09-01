package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.dto.MalariaObservationResponse;
import com.eoha.eoha_backend.service.MalariaObservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @deprecated Legacy path-variable routes. Use {@link MalariaObservationV1Controller}
 * ({@code /api/v1/malaria-observations} with {@code ?zoneId=&date=&page=&size=}) instead.
 * These aliases are retained for backward compatibility and will be removed in a future
 * major version.
 */
@Deprecated
@RestController
@RequestMapping("/api/malaria-observations")
public class MalariaObservationController {

    private final MalariaObservationService malariaObservationService;

    public MalariaObservationController(MalariaObservationService malariaObservationService) {
        this.malariaObservationService = malariaObservationService;
    }

    @Deprecated
    @GetMapping
    public ResponseEntity<ApiResponse<List<MalariaObservationResponse>>> getAllObservations() {
        return ResponseEntity.ok(ApiResponse.success(
                malariaObservationService.searchObservations(null, null)));
    }

    @Deprecated
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MalariaObservationResponse>> getObservationById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(malariaObservationService.getObservationById(id)));
    }

    @Deprecated
    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<ApiResponse<List<MalariaObservationResponse>>> getObservationsByZone(
            @PathVariable Integer zoneId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                malariaObservationService.searchObservations(zoneId, null)));
    }

    @Deprecated
    @GetMapping("/date/{dateRecorded}")
    public ResponseEntity<ApiResponse<List<MalariaObservationResponse>>> getObservationsByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateRecorded
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                malariaObservationService.searchObservations(null, dateRecorded)));
    }

    @Deprecated
    @GetMapping("/zone/{zoneId}/date/{dateRecorded}")
    public ResponseEntity<ApiResponse<List<MalariaObservationResponse>>> getObservationsByZoneAndDate(
            @PathVariable Integer zoneId,
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateRecorded
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                malariaObservationService.searchObservations(zoneId, dateRecorded)));
    }
}
