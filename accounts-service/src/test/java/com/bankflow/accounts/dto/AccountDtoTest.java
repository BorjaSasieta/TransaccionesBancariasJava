package com.bankflow.accounts.dto;

import com.bankflow.accounts.entity.Account;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountDtoTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyDto() {
        AccountDto dto = new AccountDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getIban()).isNull();
        assertThat(dto.getOwner()).isNull();
        assertThat(dto.getBalance()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        AccountDto dto = new AccountDto(1L, "ES123", "Ana", 100L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getIban()).isEqualTo("ES123");
        assertThat(dto.getOwner()).isEqualTo("Ana");
        assertThat(dto.getBalance()).isEqualTo(100L);
    }

    @Test
    void setters_shouldUpdateFields() {
        AccountDto dto = new AccountDto();
        dto.setId(2L);
        dto.setIban("ES456");
        dto.setOwner("Pedro");
        dto.setBalance(250L);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getIban()).isEqualTo("ES456");
        assertThat(dto.getOwner()).isEqualTo("Pedro");
        assertThat(dto.getBalance()).isEqualTo(250L);
    }

    @Test
    void from_shouldMapAccountToDto() {
        Account account = new Account(5L, "ES777", "Luis", 250L);

        AccountDto dto = AccountDto.from(account);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getIban()).isEqualTo("ES777");
        assertThat(dto.getOwner()).isEqualTo("Luis");
        assertThat(dto.getBalance()).isEqualTo(250L);
    }
}