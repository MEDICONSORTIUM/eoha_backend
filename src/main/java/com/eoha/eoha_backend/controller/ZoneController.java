package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.model.Zone;
import com.eoha.eoha_backend.service.ZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Zone>>> getAllZones() {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getAllZones()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Zone>> getZoneById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getZoneById(id)));
    }

    @GetMapping("/province/{provinceName}")
    public ResponseEntity<ApiResponse<List<Zone>>> getZonesByProvince(@PathVariable String provinceName) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getZonesByProvince(provinceName)));
    }

    @GetMapping("/municipality/{municipalityName}")
    public ResponseEntity<ApiResponse<List<Zone>>> getZonesByMunicipality(@PathVariable String municipalityName) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getZonesByMunicipality(municipalityName)));
    }
}