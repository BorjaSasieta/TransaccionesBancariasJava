package com.bankflow.audit.event;

import com.bankflow.audit.service.AuditService;
import com.bankflow.events.AccountCreatedEvent;
import com.bankflow.events.TransferCompletedEvent;
import com.bankflow.events.TransferCreatedEvent;
import com.bankflow.events.TransferFailedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class AuditEventListener {

    private final AuditService auditService;

    public AuditEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccountCreated(AccountCreatedEvent event) {
        auditService.record("ACCOUNT_CREATED", "Account", event.accountId(), Map.of(
                "owner", event.owner(),
                "iban", event.iban(),
                "balance", event.balance()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCreated(TransferCreatedEvent event) {
        auditService.record("TRANSFER_CREATED", "Transfer", event.transferId(), Map.of(
                "fromAccountId", event.fromAccountId(),
                "toAccountId", event.toAccountId(),
                "amount", event.amount(),
                "currency", event.currency()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(TransferCompletedEvent event) {
        auditService.record("TRANSFER_COMPLETED", "Transfer", event.transferId(), Map.of(
                "fromAccountId", event.fromAccountId(),
                "toAccountId", event.toAccountId(),
                "amount", event.amount(),
                "currency", event.currency()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferFailed(TransferFailedEvent event) {
        auditService.record("TRANSFER_FAILED", "Transfer", event.transferId(), Map.of(
                "fromAccountId", event.fromAccountId(),
                "toAccountId", event.toAccountId(),
                "reason", event.reason()));
    }
}