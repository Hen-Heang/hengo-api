package com.heang.koriaibackend.domain.health.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class HealthReadinessService {

    private static final int VALIDATION_TIMEOUT_SECONDS = 2;

    private final DataSource dataSource;

    public HealthReadinessService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isDatabaseReady() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(VALIDATION_TIMEOUT_SECONDS);
        } catch (SQLException ex) {
            return false;
        }
    }
}
