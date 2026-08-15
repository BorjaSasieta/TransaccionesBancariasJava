package com.bankflow.transfers.service;

import com.bankflow.PostgresTestContainerBase;
import com.bankflow.accounts.entity.Account;
import com.bankflow.accounts.repository.AccountRepository;
import com.bankflow.audit.entity.AuditEvent;
import com.bankflow.audit.repository.AuditEventRepository;
import com.bankflow.transfers.entity.Transfer;
import com.bankflow.transfers.entity.TransferStatus;
import com.bankflow.transfers.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WithMockUser
class TransferServiceIT extends PostgresTestContainerBase {

    @Autowired TransferService transferService;
    @Autowired AccountRepository accountRepository;
    @Autowired TransferRepository transferRepository;
    @Autowired AuditEventRepository auditEventRepository;

    @Test
    void transfer_shouldDebitFromAndCreditTo() {
        Account from = accountRepository.save(account("ES_IT_A", new BigDecimal("1000.00")));
        Account to = accountRepository.save(account("ES_IT_B", new BigDecimal("500.00")));

        Transfer result = transferService.createTransfer(new Transfer(from.getId(), to.getId(),
                new BigDecimal("100.00"), "EUR", "it-1", "ref")).transfer();

        assertThat(result.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(balanceOf(from.getId())).isEqualByComparingTo(new BigDecimal("900.00"));
        assertThat(balanceOf(to.getId())).isEqualByComparingTo(new BigDecimal("600.00"));
    }

    @Test
    void transfer_insufficientFunds_shouldMarkFailed() {
        Account from = accountRepository.save(account("ES_IT_C", new BigDecimal("50.00")));
        Account to = accountRepository.save(account("ES_IT_D", new BigDecimal("500.00")));

        Transfer result = transferService.createTransfer(new Transfer(from.getId(), to.getId(),
                new BigDecimal("100.00"), "EUR", "it-2", "ref")).transfer();

        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getErrorMessage()).isEqualTo("Insufficient funds");
        assertThat(balanceOf(from.getId())).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(balanceOf(to.getId())).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    void transfer_sameAccount_shouldThrow() {
        Account a = accountRepository.save(account("ES_IT_E", new BigDecimal("100.00")));

        assertThatThrownBy(() -> transferService.createTransfer(new Transfer(a.getId(), a.getId(),
                new BigDecimal("10.00"), "EUR", "it-3", "ref")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different");
    }

    @Test
    void transfer_sameIdempotencyKey_shouldReturnExisting() {
        Account from = accountRepository.save(account("ES_IT_F", new BigDecimal("1000.00")));
        Account to = accountRepository.save(account("ES_IT_G", new BigDecimal("0.00")));

        Transfer first = transferService.createTransfer(new Transfer(from.getId(), to.getId(),
                new BigDecimal("50.00"), "EUR", "it-key-1", "ref")).transfer();
        TransferResult second = transferService.createTransfer(new Transfer(from.getId(), to.getId(),
                new BigDecimal("50.00"), "EUR", "it-key-1", "ref"));

        assertThat(second.replayed()).isTrue();
        assertThat(second.transfer().getId()).isEqualTo(first.getId());
        assertThat(second.transfer().getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(transferRepository.findByIdempotencyKey("it-key-1")).isPresent();
        assertThat(balanceOf(from.getId())).isEqualByComparingTo(new BigDecimal("950.00"));
    }

    @Test
    void concurrentTransfers_shouldNotOverdraw() throws Exception {
        Account from = accountRepository.save(account("ES_IT_CONC_A", new BigDecimal("150.00")));
        Account to = accountRepository.save(account("ES_IT_CONC_B", new BigDecimal("0.00")));

        int n = 2;
        ExecutorService pool = Executors.newFixedThreadPool(n);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Transfer>> futures = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            final int idx = i;
            futures.add(pool.submit(() -> {
                start.await();
                return transferService.createTransfer(new Transfer(from.getId(), to.getId(),
                        new BigDecimal("100.00"), "EUR", "it-conc-" + idx, "ref")).transfer();
            }));
        }
        start.countDown();
        List<Transfer> results = new ArrayList<>();
        for (Future<Transfer> f : futures) {
            results.add(f.get(30, TimeUnit.SECONDS));
        }
        pool.shutdown();

        long completed = results.stream().filter(t -> t.getStatus() == TransferStatus.COMPLETED).count();
        long failed = results.stream().filter(t -> t.getStatus() == TransferStatus.FAILED).count();

        assertThat(completed).isEqualTo(1);
        assertThat(failed).isEqualTo(1);
        assertThat(balanceOf(from.getId())).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(balanceOf(to.getId())).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void transfer_shouldWriteAuditEvents() {
        Account from = accountRepository.save(account("ES_IT_AUDIT_A", new BigDecimal("1000.00")));
        Account to = accountRepository.save(account("ES_IT_AUDIT_B", new BigDecimal("500.00")));

        transferService.createTransfer(new Transfer(from.getId(), to.getId(),
                new BigDecimal("100.00"), "EUR", "it-audit-1", "ref"));

        List<AuditEvent> events = auditEventRepository.findAll();
        assertThat(events).extracting(AuditEvent::getEventType)
                .contains("TRANSFER_CREATED", "TRANSFER_COMPLETED");
        assertThat(events).extracting(AuditEvent::getEntityType).containsOnly("Transfer");
    }

    private Account account(String iban, BigDecimal balance) {
        Account a = new Account();
        a.setIban(iban);
        a.setOwner("IT Owner");
        a.setBalance(balance);
        return a;
    }

    private BigDecimal balanceOf(Long id) {
        return accountRepository.findById(id).orElseThrow().getBalance();
    }
}