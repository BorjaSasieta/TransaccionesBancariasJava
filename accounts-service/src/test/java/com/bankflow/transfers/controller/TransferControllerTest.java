package com.bankflow.transfers.controller;

import com.bankflow.transfers.entity.Transfer;
import com.bankflow.transfers.entity.TransferStatus;
import com.bankflow.transfers.exception.TransferInProgressException;
import com.bankflow.transfers.service.TransferResult;
import com.bankflow.transfers.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean TransferService transferService;

    @Test
    void createTransfer_shouldReturn201WithLocation() throws Exception {
        Transfer created = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-1", "ref");
        created.setId(42L);
        created.setStatus(TransferStatus.COMPLETED);
        when(transferService.createTransfer(any(Transfer.class)))
                .thenReturn(new TransferResult(created, false));

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountId\":1,\"toAccountId\":2,\"amount\":100.00,\"reference\":\"ref\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/transfers/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void createTransfer_replayed_shouldReturn200() throws Exception {
        Transfer existing = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-1", "ref");
        existing.setId(42L);
        existing.setStatus(TransferStatus.COMPLETED);
        when(transferService.createTransfer(any(Transfer.class)))
                .thenReturn(new TransferResult(existing, true));

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountId\":1,\"toAccountId\":2,\"amount\":100.00,\"reference\":\"ref\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void createTransfer_inProgress_shouldReturn202() throws Exception {
        when(transferService.createTransfer(any(Transfer.class)))
                .thenThrow(new TransferInProgressException(42L));

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountId\":1,\"toAccountId\":2,\"amount\":100.00,\"reference\":\"ref\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.error").value("TRANSFER_IN_PROGRESS"));
    }

    @Test
    void createTransfer_withNegativeAmount_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountId\":1,\"toAccountId\":2,\"amount\":-5.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransfer_existing_shouldReturn200() throws Exception {
        Transfer t = new Transfer(1L, 2L, new BigDecimal("100.00"), "EUR", "key-3", "ref");
        t.setId(7L);
        t.setStatus(TransferStatus.COMPLETED);
        when(transferService.getTransfer(7L)).thenReturn(Optional.of(t));

        mockMvc.perform(get("/api/v1/transfers/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.fromAccountId").value(1))
                .andExpect(jsonPath("$.toAccountId").value(2))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getTransfer_missing_shouldReturn404() throws Exception {
        when(transferService.getTransfer(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/transfers/999"))
                .andExpect(status().isNotFound());
    }
}