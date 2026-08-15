package com.bankflow.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BankingHealthIndicatorTest {

    DataSource dataSource;
    BankingHealthIndicator indicator;

    @BeforeEach
    void setUp() {
        dataSource = mock(DataSource.class);
        indicator = new BankingHealthIndicator(dataSource);
    }

    @Test
    void health_whenDbAvailable_shouldBeUp() throws SQLException {
        Connection connection = mock(Connection.class);
        when(connection.isValid(1)).thenReturn(true);
        when(dataSource.getConnection()).thenReturn(connection);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void health_whenConnectionInvalid_shouldBeDown() throws SQLException {
        Connection connection = mock(Connection.class);
        when(connection.isValid(1)).thenReturn(false);
        when(dataSource.getConnection()).thenReturn(connection);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("database");
    }

    @Test
    void health_whenDbUnavailable_shouldBeDownWithError() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("error");
    }
}