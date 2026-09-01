package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.dto.ZoneResponse;
import com.eoha.eoha_backend.service.ZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @deprecated Legacy path-variable routes. Use {@link ZoneV1Controller}
 * ({@code /api/v1/zones} with {@code ?province=&municipality=&page=&size=}) instead.
 * These aliases are retained for backward compatibility and will be removed in a future
 * major version.
 */
@Deprecated
@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @Deprecated
    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getAllZones() {
        return ResponseEntity.ok(ApiResponse.success(zoneService.searchZones(null, null)));
    }

    @Deprecated
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getZoneById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getZoneById(id)));
    }

    @Deprecated
    @GetMapping("/province/{provinceName}")
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getZonesByProvince(@PathVariable String provinceName) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.searchZones(provinceName, null)));
    }

    @Deprecated
    @GetMapping("/municipality/{municipalityName}")
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getZonesByMunicipality(@PathVariable String municipalityName) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.searchZones(null, municipalityName)));
    }
}
