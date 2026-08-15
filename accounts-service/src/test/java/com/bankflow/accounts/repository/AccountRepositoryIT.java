package com.bankflow.accounts.repository;

import com.bankflow.PostgresTestContainerBase;
import com.bankflow.accounts.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WithMockUser
class AccountRepositoryIT extends PostgresTestContainerBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void whenSave_thenFindById() {
        Account a = new Account();
        a.setIban("ES0000000000000000000000");
        a.setOwner("Test Owner");

        Account saved = accountRepository.save(a);
        Account found = accountRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("ES0000000000000000000000", found.getIban());
        assertEquals("Test Owner", found.getOwner());
    }

    @Test
    void whenFindByIban_thenReturnAccount() {
        Account a = new Account();
        a.setIban("ES1111111111111111111111");
        a.setOwner("Owner By Iban");
        accountRepository.save(a);

        Account found = accountRepository.findByIban("ES1111111111111111111111").orElse(null);

        assertNotNull(found);
        assertEquals("ES1111111111111111111111", found.getIban());
        assertEquals("Owner By Iban", found.getOwner());
    }

    @Test
    void whenFindByIbanUnknown_thenEmpty() {
        assertTrue(accountRepository.findByIban("ES0000000000000000000999").isEmpty());
    }
}