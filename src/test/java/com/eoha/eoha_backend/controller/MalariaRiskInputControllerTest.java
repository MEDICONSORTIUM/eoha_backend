package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.exception.GlobalExceptionHandler;
import com.eoha.eoha_backend.service.MalariaRiskInputService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MalariaRiskInputControllerTest {
    private MalariaRiskInputService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(MalariaRiskInputService.class);
        mvc = MockMvcBuilders.standaloneSetup(new MalariaRiskInputController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void exposesInputArrayAndPreservesDateAndMissingMeasurement() throws Exception {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("region_id", 2);
        row.put("region_code", "93301001");
        row.put("observation_month", "2026-01-01");
        row.put("soil_moisture", 0.3518);
        row.put("ndwi", null);
        when(service.readInputs()).thenReturn(List.of(row));

        mvc.perform(get("/api/malaria-risk-inputs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].region_code").value("93301001"))
                .andExpect(jsonPath("$.data[0].observation_month").value("2026-01-01"))
                .andExpect(jsonPath("$.data[0].ndwi").value(nullValue()))
                .andExpect(jsonPath("$.message").value(nullValue()));
        verify(service).readInputs();
    }

    @Test
    void databaseFailureReturnsUnavailableWithoutLeakingConnectionDetails() throws Exception {
        when(service.readInputs()).thenThrow(new DataAccessResourceFailureException("private connection detail"));

        mvc.perform(get("/api/malaria-risk-inputs"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Database query failed"));
    }
}
