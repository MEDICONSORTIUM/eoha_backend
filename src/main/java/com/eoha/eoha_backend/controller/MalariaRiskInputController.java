package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.service.MalariaRiskInputService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MalariaRiskInputController {
    private final MalariaRiskInputService service;

    /**
     * MalariaRiskInputController()
     * ----------------------------
     * Receive the database projection service used by the scoring-input route.
     *
     * Parameters:
     * -----------
     * service: MalariaRiskInputService — Joined measurement reader.
     */
    public MalariaRiskInputController(MalariaRiskInputService service) {
        this.service = service;
    }

    /**
     * getInputs()
     * -----------
     * Return real database inputs for frontend malaria scoring. This endpoint
     * exposes measurements, not model forecasts or frontend-calculated scores.
     *
     * Parameters:
     * -----------
     * none
     *
     * Returns:
     * --------
     * ApiResponse<List<Map<String, Object>>> — The frontend's array envelope.
     */
    @GetMapping("/api/malaria-risk-inputs")
    public ApiResponse<List<Map<String, Object>>> getInputs() {
        return ApiResponse.success(service.readInputs());
    }
}
