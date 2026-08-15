package com.bankflow.accounts.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String iban;

    @Column(name = "owner_name")
    private String owner;

    private Long balance = 0L;

    // Constructors, getters, setters
    public Account() {}
    public Account(Long id, String iban, String owner, Long balance) {
        this.id = id; this.iban = iban; this.owner = owner; this.balance = balance;
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