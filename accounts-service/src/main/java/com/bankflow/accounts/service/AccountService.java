package com.bankflow.accounts.service;

import com.bankflow.accounts.entity.Account;
import com.bankflow.accounts.repository.AccountRepository;
import com.bankflow.events.AccountCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public AccountService(AccountRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Account createAccount(Account account) {
        Account saved = repository.save(account);
        eventPublisher.publishEvent(new AccountCreatedEvent(saved.getId(), saved.getOwner(),
                saved.getIban(), saved.getBalance()));
        return saved;
    }

    public Optional<Account> getAccount(Long id) {
        return repository.findById(id);
    }
}