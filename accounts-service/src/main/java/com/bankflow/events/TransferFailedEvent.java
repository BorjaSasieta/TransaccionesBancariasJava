package com.bankflow.events;

public record TransferFailedEvent(Long transferId, Long fromAccountId, Long toAccountId, String reason) {
}