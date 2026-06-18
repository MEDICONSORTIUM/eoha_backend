package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.model.MalariaObservation;
import com.eoha.eoha_backend.service.MalariaObservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MalariaObservationController.class)
class MalariaObservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MalariaObservationService malariaObservationService;

    @Test
    void getAllObservations_returns200() throws Exception {
        MalariaObservation obs = new MalariaObservation();
        obs.setEnvRecordId(1);
        when(malariaObservationService.getAllObservations()).thenReturn(List.of(obs));

        mockMvc.perform(get("/api/malaria-observations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getObservationById_found_returns200() throws Exception {
        MalariaObservation obs = new MalariaObservation();
        obs.setEnvRecordId(1);
        when(malariaObservationService.getObservationById(1)).thenReturn(obs);

        mockMvc.perform(get("/api/malaria-observations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getObservationById_notFound_propagatesException() {
        when(malariaObservationService.getObservationById(99))
                .thenThrow(new RuntimeException("Malaria observation not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/malaria-observations/99")))
                .hasMessageContaining("Malaria observation not found");
    }

    @Test
    void getObservationsByZone_returns200() throws Exception {
        MalariaObservation obs = new MalariaObservation();
        obs.setZoneId(1);
        when(malariaObservationService.getObservationsByZone(1)).thenReturn(List.of(obs));

        mockMvc.perform(get("/api/malaria-observations/zone/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getObservationsByDate_returns200() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 15);
        MalariaObservation obs = new MalariaObservation();
        obs.setDateRecorded(date);
        when(malariaObservationService.getObservationsByDate(date)).thenReturn(List.of(obs));

        mockMvc.perform(get("/api/malaria-observations/date/2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getObservationsByZoneAndDate_returns200() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 15);
        MalariaObservation obs = new MalariaObservation();
        obs.setZoneId(1);
        obs.setDateRecorded(date);
        when(malariaObservationService.getObservationsByZoneAndDate(1, date)).thenReturn(List.of(obs));

        mockMvc.perform(get("/api/malaria-observations/zone/1/date/2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
