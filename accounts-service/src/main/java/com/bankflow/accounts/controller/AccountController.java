package com.bankflow.accounts.controller;

import com.bankflow.accounts.dto.AccountDto;
import com.bankflow.accounts.entity.Account;
import com.bankflow.accounts.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody AccountDto dto, UriComponentsBuilder uriBuilder) {
        Account a = new Account(null, dto.getIban(), dto.getOwner(), dto.getBalance());
        Account created = service.createAccount(a);
        return ResponseEntity.created(
                        uriBuilder.path("/api/v1/accounts/{id}").buildAndExpand(created.getId()).toUri())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> get(@PathVariable Long id) {
        return service.getAccount(id)
                .map(account -> ResponseEntity.ok(AccountDto.from(account)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}