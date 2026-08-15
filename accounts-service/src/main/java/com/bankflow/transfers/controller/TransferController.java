package com.bankflow.transfers.controller;

import com.bankflow.transfers.dto.TransferRequestDto;
import com.bankflow.transfers.dto.TransferResponseDto;
import com.bankflow.transfers.entity.Transfer;
import com.bankflow.transfers.service.TransferResult;
import com.bankflow.transfers.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponseDto> createTransfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferRequestDto request) {
        Transfer transfer = new Transfer(request.getFromAccountId(), request.getToAccountId(),
                request.getAmount(), request.getCurrency(), idempotencyKey, request.getReference());
        TransferResult result = transferService.createTransfer(transfer);
        Transfer created = result.transfer();
        if (result.replayed()) {
            return ResponseEntity.ok(TransferResponseDto.from(created));
        }
        return ResponseEntity
                .created(URI.create("/api/v1/transfers/" + created.getId()))
                .body(TransferResponseDto.from(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferResponseDto> getTransfer(@PathVariable("id") Long id) {
        return transferService.getTransfer(id)
                .map(t -> ResponseEntity.ok(TransferResponseDto.from(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}