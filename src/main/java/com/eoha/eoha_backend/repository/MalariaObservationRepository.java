package com.eoha.eoha_backend.repository;

import com.eoha.eoha_backend.model.MalariaObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MalariaObservationRepository extends JpaRepository<MalariaObservation, Integer> {

    List<MalariaObservation> findByZoneId(Integer zoneId);

    List<MalariaObservation> findByDateRecorded(LocalDate dateRecorded);

    List<MalariaObservation> findByZoneIdAndDateRecorded(Integer zoneId, LocalDate dateRecorded);
}