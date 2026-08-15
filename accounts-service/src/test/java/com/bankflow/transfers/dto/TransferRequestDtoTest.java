package com.bankflow.transfers.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferRequestDtoTest {

    @Test
    void noArgsConstructor_shouldCreateDtoWithDefaultCurrency() {
        TransferRequestDto dto = new TransferRequestDto();

        assertThat(dto.getFromAccountId()).isNull();
        assertThat(dto.getToAccountId()).isNull();
        assertThat(dto.getAmount()).isNull();
        assertThat(dto.getCurrency()).isEqualTo("EUR");
        assertThat(dto.getReference()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        TransferRequestDto dto = new TransferRequestDto(1L, 2L, new BigDecimal("50.00"), "EUR", "ref-1");

        assertThat(dto.getFromAccountId()).isEqualTo(1L);
        assertThat(dto.getToAccountId()).isEqualTo(2L);
        assertThat(dto.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(dto.getCurrency()).isEqualTo("EUR");
        assertThat(dto.getReference()).isEqualTo("ref-1");
    }

    @Test
    void setters_shouldUpdateFields() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromAccountId(3L);
        dto.setToAccountId(4L);
        dto.setAmount(new BigDecimal("10.00"));
        dto.setCurrency("USD");
        dto.setReference("ref-2");

        assertThat(dto.getFromAccountId()).isEqualTo(3L);
        assertThat(dto.getToAccountId()).isEqualTo(4L);
        assertThat(dto.getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(dto.getCurrency()).isEqualTo("USD");
        assertThat(dto.getReference()).isEqualTo("ref-2");
    }
}