package com.eoha.eoha_backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class NcdRiskInputServiceTest {
    @Test void repeatedRequestsReuseOnlySuccessfulResults() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        List<Map<String, Object>> rows = List.of(Map.of("region_id", 2));
        when(jdbc.queryForList(anyString())).thenThrow(new IllegalStateException("temporary failure")).thenReturn(rows);
        var service = new NcdRiskInputService(jdbc);
        assertThrows(IllegalStateException.class, service::readInputs);
        assertSame(rows, service.readInputs());
        assertSame(rows, service.readInputs());
        verify(jdbc, times(2)).queryForList(anyString());
    }
}
