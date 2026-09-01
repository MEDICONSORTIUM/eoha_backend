package com.eoha.eoha_backend.dto;

import java.time.Instant;
import java.util.List;

/**
 * Runtime metadata for the status dashboard served at {@code /}.
 */
public record InfoResponse(
        String name,
        String version,
        String apiVersion,
        Instant startedAt,
        List<String> corsAllowedOrigins
) {
}
