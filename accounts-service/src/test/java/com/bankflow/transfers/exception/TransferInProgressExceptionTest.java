package com.bankflow.transfers.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransferInProgressExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        Long transferId = 42L;
        TransferInProgressException ex = new TransferInProgressException(transferId);

        assertThat(ex.getMessage()).isEqualTo("Transfer with id " + transferId + " is already in progress");
    }
}