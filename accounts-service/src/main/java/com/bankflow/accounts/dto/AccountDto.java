package com.bankflow.accounts.dto;

import com.bankflow.accounts.entity.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {
    private Long id;
    private String iban;
    private String owner;
    private BigDecimal balance;

    public AccountDto(Long id, String iban, String owner, BigDecimal balance) {
        this.id = id; this.iban = iban; this.owner = owner; this.balance = balance;
    }

    public static AccountDto from(Account account) {
        return new AccountDto(account.getId(), account.getIban(), account.getOwner(), account.getBalance());
    }
}