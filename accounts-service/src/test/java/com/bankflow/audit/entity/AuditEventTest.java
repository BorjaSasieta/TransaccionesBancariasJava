package com.bankflow.audit.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuditEventTest {

    @Test
    void noArgsConstructor_shouldCreateEventWithTimestamp() {
        AuditEvent event = new AuditEvent();

        assertThat(event.getId()).isNull();
        assertThat(event.getEventType()).isNull();
        assertThat(event.getEntityType()).isNull();
        assertThat(event.getEntityId()).isNull();
        assertThat(event.getCreatedAt()).isNotNull();
    }

    @Test
    void constructor_shouldSetFields() {
        AuditEvent event = new AuditEvent("TRANSFER_COMPLETED", "Account", 42L, "{\"key\":\"value\"}");

        assertThat(event.getEventType()).isEqualTo("TRANSFER_COMPLETED");
        assertThat(event.getEntityType()).isEqualTo("Account");
        assertThat(event.getEntityId()).isEqualTo(42L);
        assertThat(event.getPayload()).isEqualTo("{\"key\":\"value\"}");
    }

    @Test
    void setters_shouldUpdateFields() {
        AuditEvent event = new AuditEvent();
        event.setId(1L);
        event.setEventType("TRANSFER_FAILED");
        event.setEntityType("Transfer");
        event.setEntityId(7L);
        event.setPayload("boom");

        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getEventType()).isEqualTo("TRANSFER_FAILED");
        assertThat(event.getEntityType()).isEqualTo("Transfer");
        assertThat(event.getEntityId()).isEqualTo(7L);
        assertThat(event.getPayload()).isEqualTo("boom");
    }
}