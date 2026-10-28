package com.eoha.eoha_backend.service;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class DatabaseHealthService {

    private final JdbcTemplate jdbcTemplate;
    private final EohaTableService tableService;

    public DatabaseHealthService(JdbcTemplate jdbcTemplate, EohaTableService tableService) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableService = tableService;
    }

    public Map<String, Object> check() {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            Map<String, Object> connection = jdbcTemplate.queryForMap("""
                    SELECT current_database() AS database,
                           current_user AS username,
                           current_schema() AS current_schema
                    """);

            List<String> presentTables = jdbcTemplate.queryForList("""
                    SELECT table_name
                      FROM information_schema.tables
                     WHERE table_schema = ?
                     ORDER BY table_name
                    """, String.class, EohaTableService.SCHEMA);

            Set<String> required = new TreeSet<>(tableService.allowedTables());
            Set<String> present = new TreeSet<>(presentTables);
            Set<String> missing = new TreeSet<>(required);
            missing.removeAll(present);

            result.put("connected", true);
            result.putAll(connection);
            result.put("requiredSchema", EohaTableService.SCHEMA);
            result.put("requiredTables", required);
            result.put("presentTables", present);
            result.put("missingTables", missing);
            result.put("ready", missing.isEmpty());

            return result;
        } catch (DataAccessException exception) {
            result.put("connected", false);
            result.put("ready", false);
            result.put("requiredSchema", EohaTableService.SCHEMA);
            result.put("requiredTables", new TreeSet<>(tableService.allowedTables()));
            result.put("message", mostUsefulMessage(exception));
            return result;
        }
    }

    private String mostUsefulMessage(Exception exception) {
        Throwable current = exception;
        String message = exception.getMessage();

        while (current.getCause() != null) {
            current = current.getCause();
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                message = current.getMessage();
            }
        }

        return message == null ? "Database connection failed" : message;
    }
}
