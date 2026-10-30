package com.eoha.eoha_backend.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class MalariaRiskInputService {
    private final JdbcTemplate jdbc;

    /**
     * MalariaRiskInputService()
     * -------------------------
     * Receive the configured read-only database query helper.
     *
     * Parameters:
     * -----------
     * jdbc: JdbcTemplate — Database access configured by Spring.
     */
    public MalariaRiskInputService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final long CACHE_TTL_NANOS = 60_000_000_000L;

    private List<Map<String, Object>> cachedInputs;
    private long cachedAt;

    /**
     * readInputs()
     * ------------
     * Join existing ward/month measurements into the frontend input contract.
     * Read only mean observations; leave missing, duplicate or unsupported required
     * inputs null. Reuse successful results for 60 seconds to avoid repeating the
     * full projection for simultaneous dashboard and report requests.
     *
     * Parameters:
     * -----------
     * none
     *
     * Returns:
     * --------
     * List<Map<String, Object>> — Ward-month inputs with calendar dates and metadata.
     */
    public synchronized List<Map<String, Object>> readInputs() {
        if (cachedInputs != null && System.nanoTime() - cachedAt < CACHE_TTL_NANOS) {
            return cachedInputs;
        }
        List<Map<String, Object>> inputs = jdbc.queryForList("""
            WITH inputs AS (
                SELECT o.region_id, o.observation_month, r.region_code, r.region_name, r.province,
                       max(o.metadata->>'municipality') AS municipality,
                       max((o.metadata->>'latitude')::double precision) AS latitude,
                       max((o.metadata->>'longitude')::double precision) AS longitude,
                       CASE WHEN count(*) FILTER (WHERE v.variable_code = 'lst_c') = 1
                            THEN max(o.value) FILTER (WHERE v.variable_code = 'lst_c' AND v.unit = '°C') END AS lst_c,
                       CASE WHEN count(*) FILTER (WHERE v.variable_code = 'soil_moisture') = 1
                            THEN max(o.value) FILTER (WHERE v.variable_code = 'soil_moisture' AND v.unit = 'm3/m3') END AS soil_moisture,
                       CASE WHEN count(*) FILTER (WHERE v.variable_code = 'ndwi') = 1
                            THEN max(o.value) FILTER (WHERE v.variable_code = 'ndwi' AND v.unit = 'index') END AS ndwi,
                       CASE WHEN count(*) FILTER (WHERE v.variable_code = 'population') = 1
                            THEN max(o.value * 100.0) FILTER (
                                WHERE v.variable_code = 'population' AND v.unit = 'persons'
                                  AND d.source_code = 'WORLDPOP' AND d.spatial_resolution = '100 m'
                                  AND d.dataset_id LIKE 'WorldPop/GP/100m/%') END AS population_density_per_km2,
                       max(o.value) FILTER (WHERE v.variable_code = 'air_temp_c' AND v.unit = '°C') AS air_temp_c,
                       max(o.value * 100.0) FILTER (WHERE v.variable_code = 'agriculture_fraction' AND v.unit = 'fraction') AS agric_percentage
                  FROM eoha.environmental_observations o
                  JOIN eoha.regions r ON r.region_id = o.region_id
                  JOIN eoha.environmental_variables v ON v.variable_id = o.variable_id
                  JOIN eoha.data_sources d ON d.data_source_id = o.data_source_id
                 WHERE lower(r.region_type) = 'ward' AND o.statistic = 'mean'
                 GROUP BY o.region_id, o.observation_month, r.region_code, r.region_name, r.province
            )
            SELECT region_id, region_code, region_name, province, municipality, latitude, longitude,
                   to_char(observation_month, 'YYYY-MM-DD') AS observation_month,
                   lst_c, soil_moisture, ndwi, population_density_per_km2, air_temp_c, agric_percentage
              FROM inputs ORDER BY observation_month, region_id
            """);
        cachedInputs = inputs;
        cachedAt = System.nanoTime();
        return inputs;
    }
}
