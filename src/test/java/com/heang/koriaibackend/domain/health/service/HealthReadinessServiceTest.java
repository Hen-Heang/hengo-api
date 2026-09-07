package com.heang.koriaibackend.domain.health.service;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthReadinessServiceTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final HealthReadinessService service = new HealthReadinessService(dataSource);

    @Test
    void isDatabaseReady_ShouldReturnTrue_WhenConnectionIsValid() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        assertTrue(service.isDatabaseReady());
    }

    @Test
    void isDatabaseReady_ShouldReturnFalse_WhenConnectionIsInvalid() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(false);

        assertFalse(service.isDatabaseReady());
    }

    @Test
    void isDatabaseReady_ShouldReturnFalse_WhenConnectionFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("database unavailable"));

        assertFalse(service.isDatabaseReady());
    }
}
