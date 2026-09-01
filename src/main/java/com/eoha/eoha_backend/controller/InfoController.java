package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.config.WebConfig;
import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.dto.InfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Runtime metadata consumed by the status dashboard served at {@code /}.
 */
@RestController
public class InfoController {

    private static final String API_VERSION = "v1";

    private final Instant startedAt = Instant.now();
    private final String name;
    private final String version;
    private final WebConfig webConfig;

    public InfoController(
            @Value("${eoha.name:EOHA Backend}") String name,
            @Value("${eoha.version:0.0.1-SNAPSHOT}") String version,
            WebConfig webConfig
    ) {
        this.name = name;
        this.version = version;
        this.webConfig = webConfig;
    }

    @GetMapping("/api/info")
    public ResponseEntity<ApiResponse<InfoResponse>> info() {
        return ResponseEntity.ok(ApiResponse.success(new InfoResponse(
                name,
                version,
                API_VERSION,
                startedAt,
                webConfig.getAllowedOrigins()
        )));
    }
}
