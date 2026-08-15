package com.bankflow.transfers.service;

import com.bankflow.accounts.entity.Account;
import com.bankflow.accounts.repository.AccountRepository;
import com.bankflow.transfers.entity.Transfer;
import com.bankflow.transfers.entity.TransferStatus;
import com.bankflow.transfers.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transfer createTransfer(Transfer transfer) {
        if (transfer.getFromAccountId().equals(transfer.getToAccountId())) {
            throw new IllegalArgumentException("From and to accounts must be different");
        }
        if (transfer.getIdempotencyKey() != null) {
            Optional<Transfer> existing = transferRepository.findByIdempotencyKey(transfer.getIdempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        transfer.setStatus(TransferStatus.PENDING);
        transfer = transferRepository.save(transfer);

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
            return transferRepository.save(transfer);
        }

        from.setBalance(from.getBalance().subtract(transfer.getAmount()));
        to.setBalance(to.getBalance().add(transfer.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setUpdatedAt(OffsetDateTime.now());
        return transferRepository.save(transfer);
    }

    public Optional<Transfer> getTransfer(Long id) {
        return transferRepository.findById(id);
    }
}