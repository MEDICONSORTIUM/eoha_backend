package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.model.Zone;
import com.eoha.eoha_backend.repository.ZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @InjectMocks
    private ZoneService zoneService;

    @Test
    void getAllZones_returnsAll() {
        Zone z1 = new Zone();
        z1.setZoneId(1);
        Zone z2 = new Zone();
        z2.setZoneId(2);
        when(zoneRepository.findAll()).thenReturn(List.of(z1, z2));

        List<Zone> result = zoneService.getAllZones();

        assertThat(result).hasSize(2);
        verify(zoneRepository).findAll();
    }

    @Test
    void getZoneById_found() {
        Zone zone = new Zone();
        zone.setZoneId(1);
        when(zoneRepository.findById(1)).thenReturn(Optional.of(zone));

        Zone result = zoneService.getZoneById(1);

        assertThat(result.getZoneId()).isEqualTo(1);
    }

    @Test
    void getZoneById_notFound_throwsRuntimeException() {
        when(zoneRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.getZoneById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Zone not found");
    }

    @Test
    void getZonesByProvince_returnsFilteredList() {
        Zone zone = new Zone();
        zone.setProvinceName("Limpopo");
        when(zoneRepository.findByProvinceName("Limpopo")).thenReturn(List.of(zone));

        List<Zone> result = zoneService.getZonesByProvince("Limpopo");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProvinceName()).isEqualTo("Limpopo");
    }

    @Test
    void getZonesByMunicipality_returnsFilteredList() {
        Zone zone = new Zone();
        zone.setMunicipalityName("Tzaneen");
        when(zoneRepository.findByMunicipalityName("Tzaneen")).thenReturn(List.of(zone));

        List<Zone> result = zoneService.getZonesByMunicipality("Tzaneen");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMunicipalityName()).isEqualTo("Tzaneen");
    }
}
