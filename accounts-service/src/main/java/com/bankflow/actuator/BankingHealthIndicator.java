package com.bankflow.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class BankingHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public BankingHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up().build();
            }
            return Health.down().withDetail("database", "connection invalid").build();
        } catch (SQLException ex) {
            return Health.down(ex).build();
        }
    }
}