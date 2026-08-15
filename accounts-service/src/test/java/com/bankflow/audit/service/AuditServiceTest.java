package com.bankflow.audit.service;

import com.bankflow.audit.entity.AuditEvent;
import com.bankflow.audit.repository.AuditEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class AuditServiceTest {

    @Mock AuditEventRepository repository;
    AuditService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuditService(repository, new ObjectMapper());
    }

    @Test
    void record_shouldSaveJsonPayload() {
        service.record("TRANSFER_COMPLETED", "Transfer", 1L,
                Map.of("amount", new BigDecimal("100.00")));

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(repository).save(captor.capture());
        AuditEvent saved = captor.getValue();

        assertThat(saved.getEventType()).isEqualTo("TRANSFER_COMPLETED");
        assertThat(saved.getEntityType()).isEqualTo("Transfer");
        assertThat(saved.getEntityId()).isEqualTo(1L);
        assertThat(saved.getPayload()).contains("\"amount\":100.00");
    }

    @Test
    void record_withNullPayload_shouldSaveNullPayload() {
        service.record("TRANSFER_CREATED", "Transfer", 2L, null);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getPayload()).isNull();
    }
}