package com.eoha.eoha_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // GET "/" is intentionally not mapped here so that the static status dashboard
    // (src/main/resources/static/index.html) is served at the root.

    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}
