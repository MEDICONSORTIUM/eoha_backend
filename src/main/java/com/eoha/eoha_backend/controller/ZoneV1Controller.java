package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.dto.ZoneResponse;
import com.eoha.eoha_backend.service.ZoneService;
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
     * List zones. All filters optional.
     * <p>{@code GET /api/v1/zones?province=KwaZulu-Natal&municipality=Ethekwini&page=0&size=20}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ZoneResponse>>> getZones(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String municipality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                zoneService.searchZones(province, municipality, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getZoneById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(zoneService.getZoneById(id)));
    }
}
