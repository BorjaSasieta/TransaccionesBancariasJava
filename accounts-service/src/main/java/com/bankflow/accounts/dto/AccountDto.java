package com.bankflow.accounts.dto;

public class AccountDto {
    private String iban;
    private String owner;
    private Long balance;

    public AccountDto() {}
    public AccountDto(String iban, String owner, Long balance) {
        this.iban = iban; this.owner = owner; this.balance = balance;
    }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }
}