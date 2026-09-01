-- Indexes on the columns the API filters and searches on. Without these, every
-- /api/v1/zones?province=... and /api/v1/malaria-observations?zoneId=... call is a
-- full table scan.
--
-- Plain CREATE INDEX briefly locks writes on the table. For a large production table,
-- run the CONCURRENTLY variant instead (see IMPROVEMENTS.md) in a migration marked
-- non-transactional.

CREATE INDEX IF NOT EXISTS idx_zone_province_name
    ON zone (province_name);

CREATE INDEX IF NOT EXISTS idx_zone_municipality_name
    ON zone (municipality_name);

CREATE INDEX IF NOT EXISTS idx_malaria_observation_zone_id
    ON malaria_observation (zone_id);

CREATE INDEX IF NOT EXISTS idx_malaria_observation_date_recorded
    ON malaria_observation (date_recorded);

CREATE INDEX IF NOT EXISTS idx_malaria_observation_zone_id_date_recorded
    ON malaria_observation (zone_id, date_recorded);
