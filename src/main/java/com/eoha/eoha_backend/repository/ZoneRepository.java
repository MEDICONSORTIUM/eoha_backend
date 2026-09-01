package com.eoha.eoha_backend.repository;

import com.eoha.eoha_backend.model.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {

    /**
     * Single filtered, paginated lookup backing {@code GET /api/v1/zones}. Each filter is
     * optional — a {@code null} argument means "do not filter on this field".
     */
    @Query("""
            SELECT z FROM Zone z
            WHERE (:province IS NULL OR z.provinceName = :province)
              AND (:municipality IS NULL OR z.municipalityName = :municipality)
            """)
    Page<Zone> search(@Param("province") String province,
                      @Param("municipality") String municipality,
                      Pageable pageable);
}
