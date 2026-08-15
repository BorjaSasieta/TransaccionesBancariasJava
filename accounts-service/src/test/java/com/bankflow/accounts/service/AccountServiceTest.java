package com.bankflow.accounts.service;

import com.bankflow.accounts.entity.Account;
import com.bankflow.accounts.repository.AccountRepository;
import com.bankflow.events.AccountCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    AccountRepository repository;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @InjectMocks
    AccountService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_shouldSaveAndReturnAccount() {
        Account input = new Account(null, "IBAN123", "Alice", new BigDecimal("1000.00"));
        Account saved = new Account(1L, "IBAN123", "Alice", new BigDecimal("1000.00"));

        when(repository.save(any(Account.class))).thenReturn(saved);

        Account result = service.createAccount(input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(repository, times(1)).save(any(Account.class));
        verify(eventPublisher).publishEvent(new AccountCreatedEvent(1L, "Alice", "IBAN123", new BigDecimal("1000.00")));
    }

    @Test
    void getAccountById_existing_shouldReturn() {
        Account a = new Account(1L, "IBAN1", "Bob", new BigDecimal("500.00"));
        when(repository.findById(1L)).thenReturn(Optional.of(a));

        Optional<Account> res = service.getAccount(1L);

        assertThat(res).isPresent().contains(a);
        verify(repository).findById(1L);
    }

    @Test
    void getAccountById_missing_shouldReturnEmpty() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Account> res = service.getAccount(999L);

        assertThat(res).isEmpty();
        verify(repository).findById(999L);
    }
}