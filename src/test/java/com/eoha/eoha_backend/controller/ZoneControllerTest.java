package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.model.Zone;
import com.eoha.eoha_backend.service.ZoneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ZoneController.class)
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ZoneService zoneService;

    @Test
    void getAllZones_returns200() throws Exception {
        Zone z = new Zone();
        z.setZoneId(1);
        z.setZoneName("Zone A");
        when(zoneService.getAllZones()).thenReturn(List.of(z));

        mockMvc.perform(get("/api/zones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getZoneById_found_returns200() throws Exception {
        Zone z = new Zone();
        z.setZoneId(1);
        when(zoneService.getZoneById(1)).thenReturn(z);

        mockMvc.perform(get("/api/zones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getZoneById_notFound_propagatesException() {
        when(zoneService.getZoneById(99)).thenThrow(new RuntimeException("Zone not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/zones/99")))
                .hasMessageContaining("Zone not found");
    }

    @Test
    void getZonesByProvince_returns200() throws Exception {
        Zone z = new Zone();
        z.setProvinceName("Limpopo");
        when(zoneService.getZonesByProvince("Limpopo")).thenReturn(List.of(z));

        mockMvc.perform(get("/api/zones/province/Limpopo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getZonesByMunicipality_returns200() throws Exception {
        Zone z = new Zone();
        z.setMunicipalityName("Tzaneen");
        when(zoneService.getZonesByMunicipality("Tzaneen")).thenReturn(List.of(z));

        mockMvc.perform(get("/api/zones/municipality/Tzaneen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
