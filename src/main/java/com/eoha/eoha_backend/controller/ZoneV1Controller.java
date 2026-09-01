package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.dto.ZoneResponse;
import com.eoha.eoha_backend.service.ZoneService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/zones")
public class ZoneV1Controller {

    private final ZoneService zoneService;

    public ZoneV1Controller(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    /**
     * List zones with optional filtering and pagination.
     * <p>Example: {@code GET /api/v1/zones?province=KwaZulu-Natal&municipality=Ethekwini&page=0&size=20}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ZoneResponse>>> getZones(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String municipality,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                zoneService.searchZones(province, municipality, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getZoneById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getZoneById(id)));
    }
}
