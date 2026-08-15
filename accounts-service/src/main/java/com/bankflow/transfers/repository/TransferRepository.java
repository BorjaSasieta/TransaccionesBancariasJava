package com.bankflow.transfers.repository;

import com.bankflow.transfers.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);

    Optional<Transfer> findByIdempotencyKeyAndFromAccountId(String idempotencyKey, Long fromAccountId);
}