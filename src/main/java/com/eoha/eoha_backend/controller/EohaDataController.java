package com.eoha.eoha_backend.controller;

import com.eoha.eoha_backend.dto.ApiResponse;
import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.service.EohaTableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EohaDataController {

    private final EohaTableService tableService;

    public EohaDataController(EohaTableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/regions")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getRegions(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return page("regions", limit, offset);
    }

    @GetMapping("/environmental-observations")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getEnvironmentalObservations(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return page("environmental_observations", limit, offset);
    }

    @GetMapping("/environmental-variables")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getEnvironmentalVariables(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return page("environmental_variables", limit, offset);
    }

    @GetMapping("/data-sources")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getDataSources(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return page("data_sources", limit, offset);
    }

    @GetMapping("/etl-runs")
    public ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> getEtlRuns(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return page("etl_runs", limit, offset);
    }

    @GetMapping("/schema")
    public ApiResponse<List<Map<String, Object>>> getSchema() {
        return ApiResponse.success(tableService.describeAllTables());
    }

    private ResponseEntity<ApiResponse<PagedResponse<Map<String, Object>>>> page(
            String table,
            int limit,
            int offset
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(tableService.readPage(table, limit, offset))
        );
    }
}
