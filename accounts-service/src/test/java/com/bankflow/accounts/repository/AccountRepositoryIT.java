package com.bankflow.accounts.repository;

import com.bankflow.accounts.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WithMockUser
class AccountRepositoryIT {

    @Container
    public static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:14")
                    .withDatabaseName("bankflow")
                    .withUsername("bf_user")
                    .withPassword("bf_pass");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

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