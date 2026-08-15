package com.bankflow.transfers.service;

import com.bankflow.accounts.entity.Account;
import com.bankflow.accounts.repository.AccountRepository;
import com.bankflow.events.TransferCompletedEvent;
import com.bankflow.events.TransferCreatedEvent;
import com.bankflow.events.TransferFailedEvent;
import com.bankflow.transfers.entity.Transfer;
import com.bankflow.transfers.entity.TransferStatus;
import com.bankflow.transfers.exception.TransferInProgressException;
import com.bankflow.transfers.repository.TransferRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TransferResult createTransfer(Transfer transfer) {
        if (transfer.getFromAccountId().equals(transfer.getToAccountId())) {
            throw new IllegalArgumentException("From and to accounts must be different");
        }
        if (transfer.getIdempotencyKey() != null) {
            Optional<Transfer> existing = transferRepository
                    .findByIdempotencyKeyAndFromAccountId(transfer.getIdempotencyKey(), transfer.getFromAccountId());
            if (existing.isPresent()) {
                Transfer existingTransfer = existing.get();
                if (existingTransfer.getStatus() == TransferStatus.PENDING) {
                    throw new TransferInProgressException(existingTransfer.getId());
                }
                return new TransferResult(existingTransfer, true);
            }
        }

        transfer.setStatus(TransferStatus.PENDING);
        transfer = transferRepository.save(transfer);
        eventPublisher.publishEvent(new TransferCreatedEvent(transfer.getId(), transfer.getFromAccountId(),
                transfer.getToAccountId(), transfer.getAmount(), transfer.getCurrency()));

        final Long fromAccountId = transfer.getFromAccountId();
        final Long toAccountId = transfer.getToAccountId();
        Account from = accountRepository.findByIdForUpdate(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "From account not found: " + fromAccountId));
        Account to = accountRepository.findByIdForUpdate(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "To account not found: " + toAccountId));

        if (from.getBalance().compareTo(transfer.getAmount()) < 0) {
            transfer.setStatus(TransferStatus.FAILED);
            transfer.setErrorMessage("Insufficient funds");
            transfer.setUpdatedAt(OffsetDateTime.now());
            transfer = transferRepository.save(transfer);
            eventPublisher.publishEvent(new TransferFailedEvent(transfer.getId(), fromAccountId, toAccountId,
                    "Insufficient funds"));
            return new TransferResult(transfer, false);
        }

        from.setBalance(from.getBalance().subtract(transfer.getAmount()));
        to.setBalance(to.getBalance().add(transfer.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setUpdatedAt(OffsetDateTime.now());
        transfer = transferRepository.save(transfer);
        eventPublisher.publishEvent(new TransferCompletedEvent(transfer.getId(), fromAccountId, toAccountId,
                transfer.getAmount(), transfer.getCurrency()));
        return new TransferResult(transfer, false);
    }

    public Optional<Transfer> getTransfer(Long id) {
        return transferRepository.findById(id);
    }
}