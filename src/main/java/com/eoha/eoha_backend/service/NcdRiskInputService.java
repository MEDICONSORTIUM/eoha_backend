package com.eoha.eoha_backend.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class NcdRiskInputService {
    private final JdbcTemplate jdbc;

    /**
     * NcdRiskInputService()
     * ---------------------
     * Receive the configured read-only database query helper.
     *
     * Parameters:
     * -----------
     * jdbc: JdbcTemplate — Database access configured by Spring.
     */
    public NcdRiskInputService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final long CACHE_TTL_NANOS = 60_000_000_000L;

    private List<Map<String, Object>> cachedInputs;
    private long cachedAt;

    /**
     * readInputs()
     * ------------
     * Join existing ward/month measurements into the frontend input contract.
     * Read each variable's expected statistic; leave missing, duplicate or
     * unsupported-unit inputs null. Use NCD location metadata with malaria metadata
     * and region geometry as fallbacks. Reuse successful results for 60 seconds.
     * Annual built-up observations are not projected.
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
            WITH expected (variable_code, unit, statistic) AS (
                VALUES ('air_temp_mean_c', 'degree Celsius', 'mean'),
                       ('air_temp_daily_max_mean_c', 'degree Celsius', 'mean_daily_max'),
                       ('no2_tropospheric_column_mol_m2', 'mol/m2', 'mean'),
                       ('so2_column_mol_m2', 'mol/m2', 'mean'),
                       ('pm25_mean_ug_m3', 'ug/m3', 'mean_first_day_forecast'),
                       ('pm10_mean_ug_m3', 'ug/m3', 'mean_first_day_forecast'),
                       ('pm25_10_mean_ug_m3', 'ug/m3', 'mean_first_day_forecast'),
                       ('uv_index_14h_mean', 'index', 'mean_at_14h_sast')
            ),
            measurements AS (
                SELECT o.region_id, o.period_start AS observation_month, v.variable_code,
                       max(o.processing_metadata->>'municipality') AS municipality,
                       max((o.processing_metadata->>'latitude')::double precision) AS latitude,
                       max((o.processing_metadata->>'longitude')::double precision) AS longitude,
                       CASE WHEN count(*) = 1
                            THEN max(o.value) FILTER (WHERE v.unit = e.unit) END AS value
                  FROM ncd.environmental_observations o
                  JOIN ncd.environmental_variables v ON v.variable_id = o.variable_id
                  JOIN expected e ON e.variable_code = v.variable_code AND e.statistic = o.statistic
                  JOIN eoha.regions r ON r.region_id = o.region_id
                 WHERE lower(r.region_type) = 'ward'
                   AND o.period_start = date_trunc('month', o.period_start)::date
                   AND o.period_end = (o.period_start + interval '1 month')::date
                 GROUP BY o.region_id, o.period_start, v.variable_code
            ),
            inputs AS (
                SELECT region_id, observation_month,
                       max(municipality) AS municipality,
                       max(latitude) AS latitude,
                       max(longitude) AS longitude,
                       max(value) FILTER (WHERE variable_code = 'air_temp_mean_c') AS air_temp_mean_c,
                       max(value) FILTER (WHERE variable_code = 'air_temp_daily_max_mean_c') AS air_temp_daily_max_mean_c,
                       max(value) FILTER (WHERE variable_code = 'no2_tropospheric_column_mol_m2') AS no2_tropospheric_column_mol_m2,
                       max(value) FILTER (WHERE variable_code = 'so2_column_mol_m2') AS so2_column_mol_m2,
                       max(value) FILTER (WHERE variable_code = 'pm25_mean_ug_m3') AS pm25_mean_ug_m3,
                       max(value) FILTER (WHERE variable_code = 'pm10_mean_ug_m3') AS pm10_mean_ug_m3,
                       max(value) FILTER (WHERE variable_code = 'pm25_10_mean_ug_m3') AS pm25_10_mean_ug_m3,
                       max(value) FILTER (WHERE variable_code = 'uv_index_14h_mean') AS uv_index_14h_mean
                  FROM measurements
                 GROUP BY region_id, observation_month
            ),
            locations AS (
                SELECT o.region_id, o.observation_month,
                       max(o.metadata->>'municipality') AS municipality,
                       max((o.metadata->>'latitude')::double precision) AS latitude,
                       max((o.metadata->>'longitude')::double precision) AS longitude
                  FROM eoha.environmental_observations o
                  JOIN inputs i ON i.region_id = o.region_id AND i.observation_month = o.observation_month
                 WHERE o.statistic = 'mean'
                 GROUP BY o.region_id, o.observation_month
            )
            SELECT i.region_id, r.region_code, r.region_name, r.province,
                   coalesce(i.municipality, l.municipality) AS municipality,
                   coalesce(i.latitude, l.latitude, ST_Y(ST_PointOnSurface(r.geometry))) AS latitude,
                   coalesce(i.longitude, l.longitude, ST_X(ST_PointOnSurface(r.geometry))) AS longitude,
                   to_char(i.observation_month, 'YYYY-MM-DD') AS observation_month,
                   i.air_temp_mean_c, i.air_temp_daily_max_mean_c,
                   i.no2_tropospheric_column_mol_m2, i.so2_column_mol_m2,
                   i.pm25_mean_ug_m3, i.pm10_mean_ug_m3, i.pm25_10_mean_ug_m3, i.uv_index_14h_mean
              FROM inputs i
              JOIN eoha.regions r ON r.region_id = i.region_id
              LEFT JOIN locations l ON l.region_id = i.region_id AND l.observation_month = i.observation_month
             ORDER BY i.observation_month, i.region_id
            """);
        cachedInputs = inputs;
        cachedAt = System.nanoTime();
        return inputs;
    }
}
