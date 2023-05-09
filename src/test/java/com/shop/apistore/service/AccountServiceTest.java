package com.shop.apistore.service;

import com.shop.apistore.config.CustomPasswordEncoderConfig;
import com.shop.apistore.constraint.Role;
import com.shop.apistore.model.Account;
import com.shop.apistore.repository.AccountRepository;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.security.auth.login.AccountException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Functionalities testing")
class AccountServiceTest {

    @Mock
    private AccountRepository repositoryMock;
    @Mock
    private CustomPasswordEncoderConfig encoderConfig;

    AccountService accountService;

    Account RECORD_UNSAVED = new Account("Rayven@gmail.com", "12345");
    Account RECORD_SAVED = new Account(1L, "Rayven@gmail.com", "12345", Role.USER);
    Account RECORD_SAVED_2 = new Account(2L, "David@gmail.com", "password", Role.USER);
    Account RECORD_SAVED_3 = new Account(3L, "JaneDoe@gmail.com", "pass", Role.USER);

    @BeforeEach
    void setup() {
        accountService = new AccountService(repositoryMock, encoderConfig);
    }

    @Nested
    @DisplayName("TEST SAVE method")
    class testSaveAccount {

        @Test
        @DisplayName("Successfully SAVE account to repository")
        void savingAccountToRepositoryTest_Success() throws AccountException {

            // given

            when(repositoryMock.save(RECORD_UNSAVED)).thenReturn(RECORD_SAVED);

            // when
            Account saved = accountService.save(RECORD_UNSAVED);

            // then
            assertAll("Group assert", () -> assertEquals(RECORD_SAVED, saved),
                    () -> verify(repositoryMock, times(1)).save(RECORD_UNSAVED));
        }

        @Test
        @DisplayName("Throw Exception when saving existing email to repository")
        void savingAccountToRepositoryTest_throwsAccountException() {

            // given
            when(repositoryMock.findByEmailIgnoreCase(RECORD_UNSAVED.getEmail()))
                    .thenReturn(Optional.ofNullable(RECORD_SAVED));

            // when // then
            assertThrows(AccountException.class, () -> accountService.save(RECORD_UNSAVED));
        }
    }

    @Nested
    @DisplayName("TEST FindByEmailOrThrow method")
    class testFindByEmailOrThrow {

        @Test
        @DisplayName("DO NOT Throw when account is not found in repository")
        void findByEmailOrThrowTest_Successful() {

            // given
            String email = RECORD_UNSAVED.getEmail();
            when(repositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.ofNullable(RECORD_SAVED));

            // when
            Account account = accountService.findByEmailOrThrow(email);

            // then
            assertAll("Group assert", () -> assertTrue(Objects.nonNull(account)),
                    () -> assertEquals(email, account.getEmail()));
        }

        @Test
        @DisplayName("Throw when account is not found in repository")
        void findByEmailOrThrowTest_ThrowsUsernameNotFoundException() {

            // given
            String email = RECORD_UNSAVED.getEmail();
            when(repositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

            // when // then
            assertThrows(UsernameNotFoundException.class, () -> accountService.findByEmailOrThrow(email));
        }
    }

    @Test
    @DisplayName("TEST returning list of accounts from repository")
    void getAllAccountsTest() {

        // given
        List<Account> records = new ArrayList<>(Arrays.asList(RECORD_SAVED, RECORD_SAVED_2, RECORD_SAVED_3));

        when(repositoryMock.findAll()).thenReturn(records);

        // when
        List<Account> allAccounts = accountService.getAllAccounts();

        assertAll(() -> assertThat(allAccounts, hasSize(3)), () -> assertThat(allAccounts.size(), is(3)),
                () -> assertThat(allAccounts, not(IsEmptyCollection.empty())),
                () -> assertThat(allAccounts, is(records)), () -> assertThat(allAccounts, hasItem(RECORD_SAVED_2)));
    }

    @Test
    @DisplayName("TEST building account")
    void buildAccountTest() {

        // given
        List<Account> records = new ArrayList<>(Arrays.asList(RECORD_SAVED, RECORD_SAVED_2, RECORD_SAVED_3));

        when(repositoryMock.findAll()).thenReturn(records);

        // when
        List<Account> allAccounts = accountService.getAllAccounts();

        assertAll(() -> assertThat(allAccounts, hasSize(3)), () -> assertThat(allAccounts.size(), is(3)),
                () -> assertThat(allAccounts, not(IsEmptyCollection.empty())),
                () -> assertThat(allAccounts, is(records)), () -> assertThat(allAccounts, hasItem(RECORD_SAVED_2)));
    }
}