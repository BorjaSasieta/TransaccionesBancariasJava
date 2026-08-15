package com.bankflow.accounts.mapper;

import com.bankflow.accounts.dto.AccountDto;
import com.bankflow.accounts.entity.Account;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

    @Test
    void toDto_shouldMapAccountToDto() {
        Account account = new Account(5L, "ES777", "Luis", new BigDecimal("250.00"));

        AccountDto dto = new AccountMapper().toDto(account);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getIban()).isEqualTo("ES777");
        assertThat(dto.getOwner()).isEqualTo("Luis");
        assertThat(dto.getBalance()).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        AccountDto dto = new AccountDto(5L, "ES777", "Luis", new BigDecimal("250.00"));

        Account account = new AccountMapper().toEntity(dto);

        assertThat(account.getId()).isEqualTo(5L);
        assertThat(account.getIban()).isEqualTo("ES777");
        assertThat(account.getOwner()).isEqualTo("Luis");
        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("250.00"));
    }
}