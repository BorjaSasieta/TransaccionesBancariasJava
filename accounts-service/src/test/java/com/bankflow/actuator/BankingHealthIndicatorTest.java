package com.bankflow.actuator;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BankingHealthIndicatorTest {

    private BankingHealthIndicator healthIndicator;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = mock(HikariDataSource.class);
        healthIndicator = new BankingHealthIndicator(dataSource);
    }

    @Test
    void health_whenConnectionValid_shouldReturnUpHealth() throws Exception {
        Connection conn = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.isValid(1)).thenReturn(true);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        verify(dataSource).getConnection();
        verify(conn).isValid(1);
        verify(conn).close();                   // verificar que se cierre la conexión
        verifyNoMoreInteractions(dataSource, conn);
    }

    @Test
    void health_whenConnectionInvalid_shouldReturnDownHealth() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB not available"));

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        verify(dataSource).getConnection();
        verifyNoMoreInteractions(dataSource);
    }
}