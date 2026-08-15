package com.bankflow.accounts.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyAccountWithDefaultBalance() {
        Account account = new Account();

        assertThat(account.getId()).isNull();
        assertThat(account.getIban()).isNull();
        assertThat(account.getOwner()).isNull();
        assertThat(account.getBalance()).isZero();
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        Account account = new Account(1L, "ES123", "Ana", 100L);

        assertThat(account.getId()).isEqualTo(1L);
        assertThat(account.getIban()).isEqualTo("ES123");
        assertThat(account.getOwner()).isEqualTo("Ana");
        assertThat(account.getBalance()).isEqualTo(100L);
    }

    @Test
    void setters_shouldUpdateFields() {
        Account account = new Account();
        account.setId(2L);
        account.setIban("ES456");
        account.setOwner("Pedro");
        account.setBalance(250L);

        assertThat(account.getId()).isEqualTo(2L);
        assertThat(account.getIban()).isEqualTo("ES456");
        assertThat(account.getOwner()).isEqualTo("Pedro");
        assertThat(account.getBalance()).isEqualTo(250L);
    }
}