package com.bankflow.accounts.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountDtoTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyDto() {
        AccountDto dto = new AccountDto();

        assertThat(dto.getIban()).isNull();
        assertThat(dto.getOwner()).isNull();
        assertThat(dto.getBalance()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        AccountDto dto = new AccountDto("ES123", "Ana", 100L);

        assertThat(dto.getIban()).isEqualTo("ES123");
        assertThat(dto.getOwner()).isEqualTo("Ana");
        assertThat(dto.getBalance()).isEqualTo(100L);
    }

    @Test
    void setters_shouldUpdateFields() {
        AccountDto dto = new AccountDto();
        dto.setIban("ES456");
        dto.setOwner("Pedro");
        dto.setBalance(250L);

        assertThat(dto.getIban()).isEqualTo("ES456");
        assertThat(dto.getOwner()).isEqualTo("Pedro");
        assertThat(dto.getBalance()).isEqualTo(250L);
    }
}