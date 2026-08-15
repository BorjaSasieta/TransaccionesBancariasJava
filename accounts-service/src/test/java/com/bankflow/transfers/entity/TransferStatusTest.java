package com.bankflow.transfers.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransferStatusTest {

    @Test
    void shouldContainExpectedStatuses() {
        assertThat(TransferStatus.values()).containsExactly(
                TransferStatus.PENDING, TransferStatus.COMPLETED,
                TransferStatus.FAILED, TransferStatus.CANCELLED);
    }
}