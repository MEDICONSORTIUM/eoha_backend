package com.eoha.eoha_backend.repository;

import com.eoha.eoha_backend.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {

    List<Zone> findByProvinceName(String provinceName);

    List<Zone> findByMunicipalityName(String municipalityName);
}