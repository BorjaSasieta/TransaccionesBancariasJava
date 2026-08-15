package com.bankflow.audit.event;

import com.bankflow.audit.service.AuditService;
import com.bankflow.events.AccountCreatedEvent;
import com.bankflow.events.TransferCompletedEvent;
import com.bankflow.events.TransferCreatedEvent;
import com.bankflow.events.TransferFailedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.verify;

class AuditEventListenerTest {

    @Mock AuditService auditService;
    AuditEventListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new AuditEventListener(auditService);
    }

    @Test
    void onAccountCreated_shouldRecord() {
        listener.onAccountCreated(new AccountCreatedEvent(1L, "Alice", "IBAN123", new BigDecimal("1000.00")));

        verify(auditService).record("ACCOUNT_CREATED", "Account", 1L,
                Map.of("owner", "Alice", "iban", "IBAN123", "balance", new BigDecimal("1000.00")));
    }

    @Test
    void onTransferCreated_shouldRecord() {
        listener.onTransferCreated(new TransferCreatedEvent(1L, 2L, 3L,
                new BigDecimal("100.00"), "EUR"));

        verify(auditService).record("TRANSFER_CREATED", "Transfer", 1L,
                Map.of("fromAccountId", 2L, "toAccountId", 3L,
                        "amount", new BigDecimal("100.00"), "currency", "EUR"));
    }

    @Test
    void onTransferCompleted_shouldRecord() {
        listener.onTransferCompleted(new TransferCompletedEvent(1L, 2L, 3L,
                new BigDecimal("100.00"), "EUR"));

        verify(auditService).record("TRANSFER_COMPLETED", "Transfer", 1L,
                Map.of("fromAccountId", 2L, "toAccountId", 3L,
                        "amount", new BigDecimal("100.00"), "currency", "EUR"));
    }

    @Test
    void onTransferFailed_shouldRecord() {
        listener.onTransferFailed(new TransferFailedEvent(1L, 2L, 3L, "Insufficient funds"));

        verify(auditService).record("TRANSFER_FAILED", "Transfer", 1L,
                Map.of("fromAccountId", 2L, "toAccountId", 3L, "reason", "Insufficient funds"));
    }
}