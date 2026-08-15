package com.bankflow.transfers.service;

import com.bankflow.transfers.entity.Transfer;

public record TransferResult(Transfer transfer, boolean replayed) {
}