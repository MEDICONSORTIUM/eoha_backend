package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.model.MalariaObservation;
import com.eoha.eoha_backend.repository.MalariaObservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MalariaObservationServiceTest {

    @Mock
    private MalariaObservationRepository malariaObservationRepository;

    @InjectMocks
    private MalariaObservationService malariaObservationService;

    @Test
    void getAllObservations_returnsAll() {
        MalariaObservation o1 = new MalariaObservation();
        o1.setEnvRecordId(1);
        MalariaObservation o2 = new MalariaObservation();
        o2.setEnvRecordId(2);
        when(malariaObservationRepository.findAll()).thenReturn(List.of(o1, o2));

        List<MalariaObservation> result = malariaObservationService.getAllObservations();

        assertThat(result).hasSize(2);
        verify(malariaObservationRepository).findAll();
    }

    @Test
    void getObservationById_found() {
        MalariaObservation obs = new MalariaObservation();
        obs.setEnvRecordId(1);
        when(malariaObservationRepository.findById(1)).thenReturn(Optional.of(obs));

        MalariaObservation result = malariaObservationService.getObservationById(1);

        assertThat(result.getEnvRecordId()).isEqualTo(1);
    }

    @Test
    void getObservationById_notFound_throwsRuntimeException() {
        when(malariaObservationRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> malariaObservationService.getObservationById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Malaria observation not found");
    }

    @Test
    void getObservationsByZone_returnsFilteredList() {
        MalariaObservation obs = new MalariaObservation();
        obs.setZoneId(5);
        when(malariaObservationRepository.findByZoneId(5)).thenReturn(List.of(obs));

        List<MalariaObservation> result = malariaObservationService.getObservationsByZone(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getZoneId()).isEqualTo(5);
    }

    @Test
    void getObservationsByDate_returnsFilteredList() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        MalariaObservation obs = new MalariaObservation();
        obs.setDateRecorded(date);
        when(malariaObservationRepository.findByDateRecorded(date)).thenReturn(List.of(obs));

        List<MalariaObservation> result = malariaObservationService.getObservationsByDate(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDateRecorded()).isEqualTo(date);
    }

    @Test
    void getObservationsByZoneAndDate_returnsFilteredList() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        MalariaObservation obs = new MalariaObservation();
        obs.setZoneId(3);
        obs.setDateRecorded(date);
        when(malariaObservationRepository.findByZoneIdAndDateRecorded(3, date)).thenReturn(List.of(obs));

        List<MalariaObservation> result = malariaObservationService.getObservationsByZoneAndDate(3, date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getZoneId()).isEqualTo(3);
    }
}
