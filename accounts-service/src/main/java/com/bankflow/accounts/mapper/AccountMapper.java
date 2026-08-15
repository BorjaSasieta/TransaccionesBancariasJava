package com.bankflow.accounts.mapper;

import com.bankflow.accounts.dto.AccountDto;
import com.bankflow.accounts.entity.Account;

public class AccountMapper {

    public AccountDto toDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getIban(),
                account.getOwner(),
                account.getBalance()
        );
    }

    public Account toEntity(AccountDto accountDto) {
        return new Account(
                accountDto.getId(),
                accountDto.getIban(),
                accountDto.getOwner(),
                accountDto.getBalance()
        );
    }
}