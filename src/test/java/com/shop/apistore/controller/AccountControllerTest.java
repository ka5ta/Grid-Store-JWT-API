package com.shop.apistore.controller;

import com.google.gson.Gson;
import com.shop.apistore.dto.ChangePasswordDTO;
import com.shop.apistore.filters.JwtRequestFilter;
import com.shop.apistore.model.Account;
import com.shop.apistore.service.AccountService;
import com.shop.apistore.utils.JwtTokenUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Account Controller Functionalities testing")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerTest {


    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtRequestFilter filter;

    @MockBean
    private AccountService accountServiceMock;

    @MockBean
    private Principal principalMock;

    private final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private static final String BASE_URL = "/shop/api/account/";
    private static final String ADMIN_GET_ALL_ACCOUNTS_URL = BASE_URL + "all";
    private static final String AUTHORIZE_CHANE_PASSWORD = BASE_URL + "change-password";


    private static final Account USER1;
    private static final Account ADMIN1;
    private static final Account USER2;

    Account RECORD_1 = new Account(1L, USER1.getEmail(), ENCODER.encode(USER1.getPassword()),
            USER1.getRole());
    Account RECORD_2 = new Account(2L, ADMIN1.getEmail(), ENCODER.encode(ADMIN1.getPassword()),
            ADMIN1.getRole());
    Account RECORD_3 = new Account(3L, USER2.getEmail(), ENCODER.encode(USER2.getPassword()),
            USER2.getRole());

    private List<Account> accountsList;
    private String ADMIN_TOKEN;
    private String USER_TOKEN;

    static {
        USER1 = new Account("user@email.com", "12345", "USER");
        ADMIN1 = new Account("admin@email.com", "password", "ADMIN");
        USER2 = new Account("JaneDoe@gmail.com", "pass", "USER");
    }

    @BeforeAll
    void init() {
        // using the web application to initiate the mock
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .addFilters(filter)
                .build();
    }

    @BeforeEach
    void setUp() {
        USER_TOKEN = "Bearer " + jwtTokenUtil.generateToken("user@email.com");
        ADMIN_TOKEN = "Bearer " + jwtTokenUtil.generateToken("admin@email.com");
        accountsList = Arrays.asList(RECORD_1, RECORD_2, RECORD_3);
    }

    @Nested
    @DisplayName("Admin get all accounts Test")
    class getAllAccountsTest {

        @Test
        @WithMockUser(username = "admin@email.com", roles = "ADMIN")
        @DisplayName("Admin get all accounts Test")
        @SneakyThrows
        void RetrieveAllAccountsTest() {

            // given
            when(accountServiceMock.getAllAccounts()).thenReturn(accountsList);

            // when
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                            .get(ADMIN_GET_ALL_ACCOUNTS_URL)
                            .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)

                    )
                    .andDo(print())

                    // then
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
        @WithMockUser(username = "user")
        @DisplayName("User requests to get all accounts Fail Test")
        void getAllAccountsByUserFailTest() throws Exception {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .get(ADMIN_GET_ALL_ACCOUNTS_URL)
                            .header(HttpHeaders.AUTHORIZATION, USER_TOKEN)
                    )

                    // then
                    .andDo(print())
                    .andExpect(jsonPath("$.error",
                            org.hamcrest.Matchers.is("Access Denied. You are not authorized to see content.")))
                    .andExpect(jsonPath("$.timestamp").value(notNullValue()))
                    .andExpect(status().isForbidden())
                    .andExpect(authenticated());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous requests to get all accounts Fail Test")
        void getAllAccountsAnonymousFailTest() throws Exception {


            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .get(ADMIN_GET_ALL_ACCOUNTS_URL)
                    )

                    // then
                    .andDo(print())
                    .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is("Access Denied. No Token ...")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(unauthenticated());
        }
    }

    @Nested
    @DisplayName("Change password Test")
    class ChangePasswordTest {

        private static final ChangePasswordDTO USER_CHANGE_PASS_INPUT;
        private static final Gson GSON;
        private static final String ChangePasswordDTO_USER;

        static {
            GSON = new Gson();
            USER_CHANGE_PASS_INPUT = new ChangePasswordDTO("myOldPassword", "myNewPassword");
            ChangePasswordDTO_USER = GSON.toJson(USER_CHANGE_PASS_INPUT);
        }

        @Test
        @WithMockUser(username = "user@email.com")
        @DisplayName("Change password User Successful Test")
        void changePasswordUserSuccessTest() throws Exception {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .post(AUTHORIZE_CHANE_PASSWORD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, USER_TOKEN)
                            .content(ChangePasswordDTO_USER)
                    )

                    // then
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("Password was changed.")));

        }

        @Test
        @WithMockUser(username = "admin@email.com", roles = {"USER", "ADMIN"})
        @DisplayName("Change password Admin Successful Test")
        void changePasswordAdminSuccessTest() throws Exception {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .post(AUTHORIZE_CHANE_PASSWORD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                            .content(ChangePasswordDTO_USER)
                    )

                    .andDo(print()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("Password was changed.")));

        }

        @Test
        @WithAnonymousUser
        @DisplayName("Not Allowed to change password Anonymous Failure Test")
        void changePasswordAnonymousFailureTest() throws Exception {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .post(AUTHORIZE_CHANE_PASSWORD)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer: eyJhbGciOiJIUzI1NiJ9")
                            .content(ChangePasswordDTO_USER)
                    )

                    // then
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is("Access Denied. No Token ...")));
        }
    }
}