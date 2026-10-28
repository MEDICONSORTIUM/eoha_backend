package com.eoha.eoha_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class EohaTableServiceTest {

    private EohaTableService service;

    @BeforeEach
    void setUp() {
        service = new EohaTableService(mock(JdbcTemplate.class));
    }

    @Test
    void rejectsUnknownTables() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.readPage("zone", 100, 0)
        );
    }

    @Test
    void rejectsInvalidPagination() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.readPage("regions", 0, 0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> service.readPage("regions", 501, 0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> service.readPage("regions", 100, -1)
        );
    }
}
