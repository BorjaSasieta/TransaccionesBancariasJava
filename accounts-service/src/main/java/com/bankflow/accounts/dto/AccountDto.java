package com.bankflow.accounts.dto;

import com.bankflow.accounts.entity.Account;

public class AccountDto {
    private Long id;
    private String iban;
    private String owner;
    private Long balance;

    public AccountDto() {}
    public AccountDto(Long id, String iban, String owner, Long balance) {
        this.id = id; this.iban = iban; this.owner = owner; this.balance = balance;
    }
    public static AccountDto from(Account account) {
        return new AccountDto(account.getId(), account.getIban(), account.getOwner(), account.getBalance());
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }
}