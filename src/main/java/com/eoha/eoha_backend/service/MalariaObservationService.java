package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.model.MalariaObservation;
import com.eoha.eoha_backend.repository.MalariaObservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MalariaObservationService {

    private final MalariaObservationRepository malariaObservationRepository;

    public MalariaObservationService(MalariaObservationRepository malariaObservationRepository) {
        this.malariaObservationRepository = malariaObservationRepository;
    }

    public List<MalariaObservation> getAllObservations() {
        return malariaObservationRepository.findAll();
    }

    public MalariaObservation getObservationById(Integer id) {
        return malariaObservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Malaria observation not found"));
    }

    public List<MalariaObservation> getObservationsByZone(Integer zoneId) {
        return malariaObservationRepository.findByZoneId(zoneId);
    }

    public List<MalariaObservation> getObservationsByDate(LocalDate dateRecorded) {
        return malariaObservationRepository.findByDateRecorded(dateRecorded);
    }

    public List<MalariaObservation> getObservationsByZoneAndDate(Integer zoneId, LocalDate dateRecorded) {
        return malariaObservationRepository.findByZoneIdAndDateRecorded(zoneId, dateRecorded);
    }
}