package com.bankflow.events;

import java.math.BigDecimal;

public record AccountCreatedEvent(Long accountId, String owner, String iban, BigDecimal balance) {
}