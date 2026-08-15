package com.bankflow.transfers.dto;

import com.bankflow.transfers.entity.Transfer;
import com.bankflow.transfers.entity.TransferStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransferResponseDto {

    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String currency;
    private TransferStatus status;
    private String reference;
    private String idempotencyKey;
    private String errorMessage;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public TransferResponseDto(Long id, Long fromAccountId, Long toAccountId, BigDecimal amount,
                               String currency, TransferStatus status, String reference,
                               String idempotencyKey, String errorMessage,
                               OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.reference = reference;
        this.idempotencyKey = idempotencyKey;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TransferResponseDto from(Transfer t) {
        return new TransferResponseDto(t.getId(), t.getFromAccountId(), t.getToAccountId(), t.getAmount(),
                t.getCurrency(), t.getStatus(), t.getReference(), t.getIdempotencyKey(),
                t.getErrorMessage(), t.getCreatedAt(), t.getUpdatedAt());
    }
}