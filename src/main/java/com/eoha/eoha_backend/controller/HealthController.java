package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.service.DatabaseHealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final DatabaseHealthService databaseHealthService;

    public HealthController(DatabaseHealthService databaseHealthService) {
        this.databaseHealthService = databaseHealthService;
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "service", "EOHA Backend",
                "status", "running"
        );
    }

    @GetMapping("/api/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("status", "UP"));
    }

    @GetMapping("/api/health/database")
    public ResponseEntity<ApiResponse<Map<String, Object>>> databaseHealth() {
        Map<String, Object> status = databaseHealthService.check();
        boolean ready = Boolean.TRUE.equals(status.get("ready"));

        ApiResponse<Map<String, Object>> response = ApiResponse.of(
                ready,
                status,
                ready ? null : "Database is not ready"
        );

        return ResponseEntity
                .status(ready ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
