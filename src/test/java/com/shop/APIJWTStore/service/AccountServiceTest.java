package com.shop.APIJWTStore.service;

import com.shop.APIJWTStore.model.Account;
import com.shop.APIJWTStore.constraint.Role;
import com.shop.APIJWTStore.repository.AccountRepository;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.security.auth.login.AccountException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@WebMvcTest(AccountService.class)
@DisplayName("Account Service Functionalities testing")
class AccountServiceTest {

    @MockBean
    AccountRepository repositoryMock;

    @Autowired
    AccountService service;

    /*Account RECORD_UNSAVED = new Account("Rayven@gmail.com", "12345", Role.USER);
    Account RECORD_SAVED = new Account(1L, "Rayven@gmail.com", "12345", Role.USER);
    Account RECORD_SAVED_2 = new Account(2L, "David@gmail.com", "password", Role.USER);
    Account RECORD_SAVED_3 = new Account(3L, "JaneDoe@gmail.com", "pass", Role.USER);


    @Nested
    @DisplayName("TEST repository SAVE method")
    class testRepoSaveAccount {
        @Test
        @DisplayName("Successfully SAVE account to repository")
        void successSavingAccountToRepositoryTest() throws AccountException {

            when(repositoryMock.save(RECORD_UNSAVED)).thenReturn(RECORD_SAVED);

            assertAll("Group assert",
                    () -> assertEquals(RECORD_SAVED, service.save(RECORD_UNSAVED)),
                    () -> verify(repositoryMock, times(1)).save(RECORD_UNSAVED)
            );
        }

        @Test
        @DisplayName("Throw Exception when save already existing email to repository")
        void failureSavingAccountToRepositoryTest() throws AccountException {

            when(repositoryMock.findByEmailIgnoreCase(RECORD_UNSAVED.getEmail())).thenReturn(Optional.ofNullable(RECORD_SAVED));
            assertAll(
                    () -> assertTrue(repositoryMock.findByEmailIgnoreCase(RECORD_UNSAVED.getEmail()).isPresent()),
                    () -> assertThrows(AccountException.class, () -> service.save(RECORD_UNSAVED))
            );

        }

        @Test
        @DisplayName("DO NOT Throw when account is not found in repository")
        void findByEmailIgnoreCaseTest() throws AccountException {

            when(repositoryMock.findByEmailIgnoreCase(RECORD_UNSAVED.getEmail())).thenReturn(Optional.empty());
            when(repositoryMock.save(RECORD_UNSAVED)).thenReturn(RECORD_SAVED);

            assertAll("Group assert",
                    () -> assertTrue(repositoryMock.findByEmailIgnoreCase(RECORD_UNSAVED.getEmail()).isEmpty()),
                    () -> assertDoesNotThrow(() -> service.save(RECORD_UNSAVED), "shouldn't throw, but exception was thrown"),
                    () -> assertEquals(RECORD_SAVED, service.save(RECORD_UNSAVED))
            );
        }
    }

    @Test
    @DisplayName("TEST returning list of accounts from repository")
    void getAllAccountsTest() {
        List<Account> records = new ArrayList<>(Arrays.asList(RECORD_SAVED, RECORD_SAVED_2, RECORD_SAVED_3));

        when(repositoryMock.findAll()).thenReturn(records);
        List<Account> actual = service.getAllAccounts();

        assertAll(
                () -> assertThat(actual, hasSize(3)),
                () -> assertThat(actual.size(), is(3)),
                () -> assertThat(actual, not(IsEmptyCollection.empty())),
                () -> assertThat(actual, is(records)),
                () -> assertThat(actual, hasItem(RECORD_SAVED_2))
        );
    }
*/
}