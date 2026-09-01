package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.dto.MalariaObservationResponse;
import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.service.MalariaObservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/malaria-observations")
public class MalariaObservationV1Controller {

    private final MalariaObservationService malariaObservationService;

    public MalariaObservationV1Controller(MalariaObservationService malariaObservationService) {
        this.malariaObservationService = malariaObservationService;
    }

    /**
     * List observations. All filters optional.
     * <p>{@code GET /api/v1/malaria-observations?zoneId=1&date=2026-03-01&page=0&size=20}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<MalariaObservationResponse>>> getObservations(
            @RequestParam(required = false) Integer zoneId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                malariaObservationService.searchObservations(zoneId, date, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MalariaObservationResponse>> getObservationById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(malariaObservationService.getObservationById(id)));
    }
}
