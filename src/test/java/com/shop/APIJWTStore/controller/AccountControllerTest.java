package com.shop.APIJWTStore.controller;

import com.google.gson.Gson;
import com.shop.APIJWTStore.dto.ChangePasswordDTO;
import com.shop.APIJWTStore.model.Account;
import com.shop.APIJWTStore.constraint.Role;
import com.shop.APIJWTStore.service.AccountService;
import com.shop.APIJWTStore.utils.JwtTokenUtil;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import javax.security.auth.login.AccountException;
import java.security.Principal;
import java.util.*;


import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("Account Controller Functionalities testing")
@AutoConfigureMockMvc
@SpringBootTest
class AccountControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @MockBean
    private AccountService accountServiceMock;

    @MockBean
    private Principal principal;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    private static final String ADMIN_GET_ALL_ACCOUNTS = "/shop/api/auth/all";
    private static final String AUTHORIZE_CHANE_PASSWORD = "/shop/api/auth/change-password";
    Account INPUT_1 = new Account("user@email.com", "12345", Collections.singleton(Role.USER));
    Account INPUT_2 = new Account("admin@email.com", "password", new HashSet<>(Arrays.asList(Role.USER, Role.ADMIN)));
    Account INPUT_3 = new Account("JaneDoe@gmail.com", "pass", Collections.singleton(Role.USER));

    Account RECORD_1 = new Account(1L, INPUT_1.getEmail(), passwordEncoder.encode(INPUT_1.getPassword()), INPUT_1.getRoles());
    Account RECORD_2 = new Account(2L, INPUT_2.getEmail(), passwordEncoder.encode(INPUT_2.getPassword()), INPUT_2.getRoles());
    Account RECORD_3 = new Account(3L, INPUT_3.getEmail(), passwordEncoder.encode(INPUT_3.getPassword()), INPUT_3.getRoles());

    List<Account> accountsList = Arrays.asList(RECORD_1, RECORD_2, RECORD_3);

    static String ADMIN_TOKEN;
    static String USER_TOKEN;


    @BeforeEach
    void beforeEach() {
        USER_TOKEN = "Bearer " + jwtTokenUtil.generateToken("user@email.com");
        ADMIN_TOKEN = "Bearer " + jwtTokenUtil.generateToken("admin@email.com");
    }


    @Nested
    @DisplayName("Admin get all accounts Test")
    class getAllAccountsTest {

        @Test
        @WithMockUser(username = "admin@email.com", roles = {"USER", "ADMIN"})
        @DisplayName("Admin get all accounts Test")
        void getAllAccountsTest() throws Exception {

            // Mock
            when(accountServiceMock.getAllAccounts()).thenReturn(accountsList);

            MvcResult result = mockMvc
                    .perform(MockMvcRequestBuilders
                            .get(ADMIN_GET_ALL_ACCOUNTS)
                            .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].email", is(RECORD_1.getEmail())))
                    .andExpect(jsonPath("$[1].email", is("admin@email.com")))
                    .andExpect(jsonPath("$[2].email", is("JaneDoe@gmail.com")))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(authenticated())
                    .andReturn();

            String contentAsString = result.getResponse().getContentAsString();
            assertTrue(contentAsString.contains("JaneDoe@gmail.com"));

        }

        @Test
        @WithMockUser(username = "user@email.com", roles = {"USER"})
        @DisplayName("User get all accounts Fail Test")
        void getAllAccountsByUserFailTest() throws Exception {

            //Mock
            when(accountServiceMock.getAllAccounts()).thenReturn(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));


            mockMvc
                    .perform(MockMvcRequestBuilders
                            .get(ADMIN_GET_ALL_ACCOUNTS)
                            .header(HttpHeaders.AUTHORIZATION, USER_TOKEN))
                    .andDo(print())
                    //.andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is("Access Denied. You are not authorized to see content.")))
                    .andExpect(jsonPath("$.timestamp").value(notNullValue()))
                    .andExpect(status().isForbidden())
                    .andExpect(authenticated());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous get all accounts Fail Test")
        void getAllAccountsAnonymousFailTest() throws Exception {
            when(accountServiceMock.getAllAccounts()).thenReturn(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .get(ADMIN_GET_ALL_ACCOUNTS))
                    .andDo(print())
                    .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is("Access Denied. No Token ...")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(unauthenticated());
        }
    }

    @Nested
    @DisplayName("Change password Test")
    class changePasswordTest {

        ChangePasswordDTO USER_CHANGE_PASS_INPUT = new ChangePasswordDTO("myOldPassword", "myNewPassword");
        Gson gson = new Gson();
        String ChangePasswordDTO_USER = gson.toJson(USER_CHANGE_PASS_INPUT);

        @Test
        @WithMockUser(username = "user@email.com", roles = {"USER"})
        @DisplayName("Change password User Successful Test")
        void changePasswordUserSuccessTest() throws Exception {
            //Mock
            doNothing().when(accountServiceMock).changePassword(USER_CHANGE_PASS_INPUT, principal);


            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post(AUTHORIZE_CHANE_PASSWORD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, USER_TOKEN)
                            .content(ChangePasswordDTO_USER))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("Password was changed.")));

        }

        @Test
        @WithMockUser(username = "admin@email.com", roles = {"USER", "ADMIN"})
        @DisplayName("Change password Admin Successful Test")
        void changePasswordAdminSuccessTest() throws Exception {
            //Mock
            doNothing().when(accountServiceMock).changePassword(USER_CHANGE_PASS_INPUT, principal);

            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post(AUTHORIZE_CHANE_PASSWORD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                            .content(ChangePasswordDTO_USER))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("Password was changed.")));

        }

        @Test
        @WithAnonymousUser
        @DisplayName("Not Allowed to change password Anonymous Failure Test")
        void changePasswordAnonymousFailureTest() throws Exception {
            //Mock
            doNothing().when(accountServiceMock).changePassword(USER_CHANGE_PASS_INPUT, principal);

            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post(AUTHORIZE_CHANE_PASSWORD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ChangePasswordDTO_USER))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is("Access Denied. No Token ...")));

        }


    }


}