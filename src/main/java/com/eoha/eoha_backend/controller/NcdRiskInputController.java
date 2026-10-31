package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.service.NcdRiskInputService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class NcdRiskInputController {
    private final NcdRiskInputService service;

    /**
     * NcdRiskInputController()
     * ------------------------
     * Receive the database projection service used by the scoring-input route.
     *
     * Parameters:
     * -----------
     * service: NcdRiskInputService — Joined measurement reader.
     */
    public NcdRiskInputController(NcdRiskInputService service) {
        this.service = service;
    }

    /**
     * getInputs()
     * -----------
     * Return real database inputs for frontend NCD scoring. This endpoint
     * exposes measurements, not frontend-calculated scores.
     *
     * Parameters:
     * -----------
     * none
     *
     * Returns:
     * --------
     * ApiResponse<List<Map<String, Object>>> — The frontend's array envelope.
     */
    @GetMapping("/api/ncd-risk-inputs")
    public ApiResponse<List<Map<String, Object>>> getInputs() {
        return ApiResponse.success(service.readInputs());
    }
}
