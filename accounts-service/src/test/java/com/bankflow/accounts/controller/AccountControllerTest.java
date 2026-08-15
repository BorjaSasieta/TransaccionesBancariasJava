package com.bankflow.accounts.controller;

import com.bankflow.accounts.entity.Account;
import com.bankflow.accounts.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService service;

    @Test
    void create_shouldReturn201AndLocationHeader() throws Exception {
        Account created = new Account(42L, "ES123", "Ana", new BigDecimal("100.00"));
        when(service.createAccount(any(Account.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"iban\":\"ES123\",\"owner\":\"Ana\",\"balance\":100}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/accounts/42")));
    }

    @Test
    void create_shouldDelegateToService() throws Exception {
        Account created = new Account(7L, "ES999", "Pedro", new BigDecimal("50.00"));
        when(service.createAccount(any(Account.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"iban\":\"ES999\",\"owner\":\"Pedro\",\"balance\":50}"))
                .andExpect(status().isCreated());

        verify(service).createAccount(any(Account.class));
    }

    @Test
    void getById_existing_shouldReturn200AndAccount() throws Exception {
        Account account = new Account(42L, "ES123", "Ana", new BigDecimal("100.00"));
        when(service.getAccount(42L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/v1/accounts/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.iban").value("ES123"))
                .andExpect(jsonPath("$.owner").value("Ana"))
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    void getById_unknown_shouldReturn404() throws Exception {
        when(service.getAccount(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/accounts/999"))
                .andExpect(status().isNotFound());
    }
}