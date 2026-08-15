package com.bankflow.accounts.dto;

import com.bankflow.accounts.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String iban;
    private String owner;
    private BigDecimal balance;

    public static AccountDto from(Account account) {
        return new AccountDto(account.getId(), account.getIban(), account.getOwner(), account.getBalance());
    }
}