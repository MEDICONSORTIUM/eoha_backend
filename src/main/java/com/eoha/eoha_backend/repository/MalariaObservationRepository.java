package com.eoha.eoha_backend.repository;

import com.eoha.eoha_backend.model.MalariaObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface MalariaObservationRepository extends JpaRepository<MalariaObservation, Integer> {

    /**
     * Single filtered, paginated lookup backing {@code GET /api/v1/malaria-observations}.
     * Each filter is optional — a {@code null} argument means "do not filter on this
     * field".
     */
    @Query("""
            SELECT o FROM MalariaObservation o
            WHERE (CAST(:zoneId AS integer) IS NULL OR o.zoneId = :zoneId)
              AND (CAST(:date AS date) IS NULL OR o.dateRecorded = :date)
            """)
    Page<MalariaObservation> search(@Param("zoneId") Integer zoneId,
                                    @Param("date") LocalDate date,
                                    Pageable pageable);
}
