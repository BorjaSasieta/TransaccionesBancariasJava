package com.bankflow.transfers.exception;

public class TransferInProgressException extends RuntimeException {

    public TransferInProgressException(Long transferId) {
        super("Transfer with id " + transferId + " is already in progress");
    }
}