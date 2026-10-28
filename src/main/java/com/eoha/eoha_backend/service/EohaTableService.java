package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.dto.PagedResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EohaTableService {

    public static final String SCHEMA = "eoha";

    private static final Set<String> ALLOWED_TABLES = Set.of(
            "regions",
            "environmental_observations",
            "environmental_variables",
            "data_sources",
            "etl_runs"
    );

    private static final int MAX_LIMIT = 500;

    private final JdbcTemplate jdbcTemplate;

    public EohaTableService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PagedResponse<Map<String, Object>> readPage(String table, int limit, int offset) {
        validateTable(table);
        validatePagination(limit, offset);

        String qualifiedTable = SCHEMA + "." + table;
        String sql = "SELECT * FROM " + qualifiedTable + " ORDER BY 1 LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM " + qualifiedTable;

        List<Map<String, Object>> items = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs),
                limit,
                offset
        );

        Long total = jdbcTemplate.queryForObject(countSql, Long.class);

        return new PagedResponse<>(
                items,
                total == null ? 0L : total,
                limit,
                offset
        );
    }

    public List<Map<String, Object>> describeAllTables() {
        String placeholders = String.join(", ", ALLOWED_TABLES.stream().map(t -> "?").toList());
        String sql = """
                SELECT table_name,
                       column_name,
                       data_type,
                       udt_name,
                       is_nullable,
                       column_default,
                       ordinal_position
                  FROM information_schema.columns
                 WHERE table_schema = ?
                   AND table_name IN (%s)
                 ORDER BY table_name, ordinal_position
                """.formatted(placeholders);

        List<Object> args = new ArrayList<>();
        args.add(SCHEMA);
        args.addAll(ALLOWED_TABLES);

        return jdbcTemplate.queryForList(sql, args.toArray());
    }

    public Set<String> allowedTables() {
        return ALLOWED_TABLES;
    }

    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        Map<String, Object> row = new LinkedHashMap<>();

        for (int column = 1; column <= metadata.getColumnCount(); column++) {
            String name = metadata.getColumnLabel(column);
            Object value = rs.getObject(column);
            row.put(name, normalizeValue(value));
        }

        return row;
    }

    private Object normalizeValue(Object value) throws SQLException {
        if (value == null) {
            return null;
        }

        if (value instanceof Array sqlArray) {
            return sqlArray.getArray();
        }

        String className = value.getClass().getName();
        if (className.startsWith("org.postgresql.")) {
            return value.toString();
        }

        return value;
    }

    private void validateTable(String table) {
        if (!ALLOWED_TABLES.contains(table)) {
            throw new IllegalArgumentException("Unsupported EOHA table: " + table);
        }
    }

    private void validatePagination(int limit, int offset) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new IllegalArgumentException("limit must be between 1 and " + MAX_LIMIT);
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be 0 or greater");
        }
    }
}
