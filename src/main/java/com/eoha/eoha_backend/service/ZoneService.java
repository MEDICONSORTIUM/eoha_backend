package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.model.Zone;
import com.eoha.eoha_backend.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Integer id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
    }

    public List<Zone> getZonesByProvince(String provinceName) {
        return zoneRepository.findByProvinceName(provinceName);
    }

    public List<Zone> getZonesByMunicipality(String municipalityName) {
        return zoneRepository.findByMunicipalityName(municipalityName);
    }
}