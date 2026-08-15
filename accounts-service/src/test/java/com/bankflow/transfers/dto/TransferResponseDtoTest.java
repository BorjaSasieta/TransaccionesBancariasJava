package com.bankflow.transfers.dto;

import com.bankflow.transfers.entity.Transfer;
import com.bankflow.transfers.entity.TransferStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferResponseDtoTest {

    @Test
    void from_shouldMapTransferToDto() {
        Transfer t = new Transfer(1L, 2L, new BigDecimal("150.75"), "EUR", "key-1", "ref-1");
        t.setId(9L);
        t.setStatus(TransferStatus.COMPLETED);

        TransferResponseDto dto = TransferResponseDto.from(t);

        assertThat(dto.getId()).isEqualTo(9L);
        assertThat(dto.getFromAccountId()).isEqualTo(1L);
        assertThat(dto.getToAccountId()).isEqualTo(2L);
        assertThat(dto.getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(dto.getCurrency()).isEqualTo("EUR");
        assertThat(dto.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(dto.getReference()).isEqualTo("ref-1");
        assertThat(dto.getIdempotencyKey()).isEqualTo("key-1");
        assertThat(dto.getCreatedAt()).isEqualTo(t.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(t.getUpdatedAt());
    }
}