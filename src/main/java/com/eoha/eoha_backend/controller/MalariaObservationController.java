package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.model.MalariaObservation;
import com.eoha.eoha_backend.service.MalariaObservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/malaria-observations")
public class MalariaObservationController {

    private final MalariaObservationService malariaObservationService;

    public MalariaObservationController(MalariaObservationService malariaObservationService) {
        this.malariaObservationService = malariaObservationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MalariaObservation>>> getAllObservations() {
        return ResponseEntity.ok(ApiResponse.success(malariaObservationService.getAllObservations()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MalariaObservation>> getObservationById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(malariaObservationService.getObservationById(id)));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<ApiResponse<List<MalariaObservation>>> getObservationsByZone(
            @PathVariable Integer zoneId
    ) {
        return ResponseEntity.ok(ApiResponse.success(malariaObservationService.getObservationsByZone(zoneId)));
    }

    @GetMapping("/date/{dateRecorded}")
    public ResponseEntity<ApiResponse<List<MalariaObservation>>> getObservationsByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateRecorded
    ) {
        return ResponseEntity.ok(ApiResponse.success(malariaObservationService.getObservationsByDate(dateRecorded)));
    }

    @GetMapping("/zone/{zoneId}/date/{dateRecorded}")
    public ResponseEntity<ApiResponse<List<MalariaObservation>>> getObservationsByZoneAndDate(
            @PathVariable Integer zoneId,
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateRecorded
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        malariaObservationService.getObservationsByZoneAndDate(zoneId, dateRecorded)
                )
        );
    }
}