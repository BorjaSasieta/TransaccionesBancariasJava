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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock AccountRepository accountRepository;
    @Mock TransferRepository transferRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @InjectMocks TransferService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(transferRepository.save(any(Transfer.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createTransfer_shouldDebitFromAndCreditTo() {
        Account from = new Account(1L, "ES1", "Ana", new BigDecimal("1000.00"));
        Account to = new Account(2L, "ES2", "Bob", new BigDecimal("500.00"));
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

        Transfer input = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", null, "ref-1");

        TransferResult result = service.createTransfer(input);

        assertThat(result.replayed()).isFalse();
        assertThat(result.transfer().getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(from.getBalance()).isEqualByComparingTo(new BigDecimal("900.00"));
        assertThat(to.getBalance()).isEqualByComparingTo(new BigDecimal("600.00"));
        verify(accountRepository, times(1)).save(from);
        verify(accountRepository, times(1)).save(to);
        verify(transferRepository, times(2)).save(any(Transfer.class));
        verify(eventPublisher).publishEvent(any(TransferCreatedEvent.class));
        verify(eventPublisher).publishEvent(any(TransferCompletedEvent.class));
    }

    @Test
    void createTransfer_insufficientFunds_shouldMarkFailed() {
        Account from = new Account(1L, "ES1", "Ana", new BigDecimal("50.00"));
        Account to = new Account(2L, "ES2", "Bob", new BigDecimal("500.00"));
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

        Transfer input = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", null, "ref-2");

        TransferResult result = service.createTransfer(input);

        assertThat(result.replayed()).isFalse();
        assertThat(result.transfer().getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.transfer().getErrorMessage()).isEqualTo("Insufficient funds");
        assertThat(from.getBalance()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(to.getBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
        verify(accountRepository, never()).save(any());
        verify(eventPublisher).publishEvent(any(TransferCreatedEvent.class));
        verify(eventPublisher).publishEvent(any(TransferFailedEvent.class));
    }

    @Test
    void createTransfer_withExistingCompletedKey_shouldReplay() {
        Transfer existing = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-1", "ref");
        existing.setStatus(TransferStatus.COMPLETED);
        when(transferRepository.findByIdempotencyKeyAndFromAccountId("key-1", 1L))
                .thenReturn(Optional.of(existing));

        Transfer input = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-1", "ref");

        TransferResult result = service.createTransfer(input);

        assertThat(result.replayed()).isTrue();
        assertThat(result.transfer()).isSameAs(existing);
        verify(transferRepository, never()).save(any(Transfer.class));
        verify(accountRepository, never()).findByIdForUpdate(anyLong());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void createTransfer_withExistingFailedKey_shouldReplay() {
        Transfer existing = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-2", "ref");
        existing.setStatus(TransferStatus.FAILED);
        existing.setErrorMessage("Insufficient funds");
        when(transferRepository.findByIdempotencyKeyAndFromAccountId("key-2", 1L))
                .thenReturn(Optional.of(existing));

        Transfer input = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-2", "ref");

        TransferResult result = service.createTransfer(input);

        assertThat(result.replayed()).isTrue();
        assertThat(result.transfer()).isSameAs(existing);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void createTransfer_withPendingKey_shouldThrow() {
        Transfer existing = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-3", "ref");
        existing.setStatus(TransferStatus.PENDING);
        when(transferRepository.findByIdempotencyKeyAndFromAccountId("key-3", 1L))
                .thenReturn(Optional.of(existing));

        Transfer input = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-3", "ref");

        assertThatThrownBy(() -> service.createTransfer(input))
                .isInstanceOf(TransferInProgressException.class);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void createTransfer_fromAndToSame_shouldThrow() {
        Transfer input = new Transfer(1L, 1L, new BigDecimal("100.00"), "EUR", null, "ref");

        assertThatThrownBy(() -> service.createTransfer(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different");

        verifyNoInteractions(transferRepository);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void getTransfer_existing_shouldReturn() {
        Transfer t = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", null, "ref");
        when(transferRepository.findById(1L)).thenReturn(Optional.of(t));

        Optional<Transfer> res = service.getTransfer(1L);

        assertThat(res).isPresent().contains(t);
        verify(transferRepository).findById(1L);
    }

    @Test
    void getTransfer_missing_shouldReturnEmpty() {
        when(transferRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Transfer> res = service.getTransfer(999L);

        assertThat(res).isEmpty();
        verify(transferRepository).findById(999L);
    }
}