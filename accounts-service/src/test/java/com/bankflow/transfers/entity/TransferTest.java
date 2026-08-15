package com.bankflow.transfers.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferTest {

    @Test
    void noArgsConstructor_shouldCreateTransferWithDefaults() {
        Transfer transfer = new Transfer();

        assertThat(transfer.getId()).isNull();
        assertThat(transfer.getFromAccountId()).isNull();
        assertThat(transfer.getToAccountId()).isNull();
        assertThat(transfer.getAmount()).isNull();
        assertThat(transfer.getCurrency()).isEqualTo("EUR");
        assertThat(transfer.getStatus()).isEqualTo(TransferStatus.PENDING);
        assertThat(transfer.getCreatedAt()).isNotNull();
        assertThat(transfer.getUpdatedAt()).isNotNull();
    }

    @Test
    void constructor_shouldSetBusinessFields() {
        Transfer transfer = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-1", "ref-1");

        assertThat(transfer.getFromAccountId()).isEqualTo(1L);
        assertThat(transfer.getToAccountId()).isEqualTo(2L);
        assertThat(transfer.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(transfer.getCurrency()).isEqualTo("EUR");
        assertThat(transfer.getIdempotencyKey()).isEqualTo("key-1");
        assertThat(transfer.getReference()).isEqualTo("ref-1");
        assertThat(transfer.getStatus()).isEqualTo(TransferStatus.PENDING);
    }

    @Test
    void setters_shouldUpdateFields() {
        Transfer transfer = new Transfer();
        transfer.setId(1L);
        transfer.setFromAccountId(10L);
        transfer.setToAccountId(20L);
        transfer.setAmount(new BigDecimal("50.00"));
        transfer.setCurrency("USD");
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setIdempotencyKey("k");
        transfer.setReference("r");
        transfer.setErrorMessage("err");

        assertThat(transfer.getId()).isEqualTo(1L);
        assertThat(transfer.getFromAccountId()).isEqualTo(10L);
        assertThat(transfer.getToAccountId()).isEqualTo(20L);
        assertThat(transfer.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(transfer.getCurrency()).isEqualTo("USD");
        assertThat(transfer.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(transfer.getIdempotencyKey()).isEqualTo("k");
        assertThat(transfer.getReference()).isEqualTo("r");
        assertThat(transfer.getErrorMessage()).isEqualTo("err");
    }
}