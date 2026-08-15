package com.bankflow.transfers.mapper;

import com.bankflow.transfers.dto.TransferResponseDto;
import com.bankflow.transfers.entity.Transfer;

public class TransferMapper {

    public TransferResponseDto toDto(Transfer transfer) {
        return new TransferResponseDto(
                transfer.getId(),
                transfer.getFromAccountId(),
                transfer.getToAccountId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getStatus(),
                transfer.getReference(),
                transfer.getIdempotencyKey(),
                transfer.getErrorMessage(),
                transfer.getCreatedAt(),
                transfer.getUpdatedAt()
        );
    }

    public Transfer toEntity(TransferResponseDto dto) {
        return new Transfer(
                dto.getFromAccountId(),
                dto.getToAccountId(),
                dto.getAmount(),
                dto.getCurrency(),
                dto.getIdempotencyKey(),
                dto.getReference()
        );
    }
}