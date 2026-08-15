package com.bankflow.events;

import java.math.BigDecimal;

public record TransferCompletedEvent(Long transferId, Long fromAccountId, Long toAccountId,
                                     BigDecimal amount, String currency) {
}