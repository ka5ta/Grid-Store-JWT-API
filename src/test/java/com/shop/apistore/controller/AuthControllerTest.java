package com.shop.apistore.controller;

import com.google.gson.Gson;
import com.shop.apistore.config.CustomPasswordEncoderConfig;
import com.shop.apistore.constraint.Role;
import com.shop.apistore.dto.JwtAuthRequest;
import com.shop.apistore.dto.RegisterAccountDTO;
import com.shop.apistore.model.Account;
import com.shop.apistore.model.JwtUserDetails;
import com.shop.apistore.service.JwtUserDetailsService;
import com.shop.apistore.utils.JwtTokenUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.login.AccountException;

import static com.shop.apistore.controller.ResponseHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@DisplayName("Authentication Controller Functionalities testing")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomPasswordEncoderConfig passwordEncoderConfig;

    @MockBean
    private JwtUserDetailsService jwtUserDetailsServiceMock;
    @MockBean
    private JwtTokenUtil jwtTokenUtilMock;

    private Account USER_ACCOUNT_1_OUTPUT;

    private Gson gson;


    @BeforeAll
    void init() {
        // using the web application to initiate the mock
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        USER_ACCOUNT_1_OUTPUT = new Account(1L, "user@email.com", passwordEncoderConfig.bCryptPasswordEncoder().encode("123456"),
                Role.USER);

        gson = new Gson();
    }

    @Nested
    @DisplayName("Register endpoint Test")
    class registerAuthControllerTest {

        // given
        final RegisterAccountDTO USER_REGISTER_ACCOUNT_DTO_1 = new RegisterAccountDTO("user@email.com", "123456", Role.USER.name());

        String json = gson.toJson(USER_REGISTER_ACCOUNT_DTO_1);

        @Test
        @DisplayName("Anonymous register success")
        @WithAnonymousUser
        @SneakyThrows
        void anonymousCanRegisterTest() {

            // given
            when(jwtUserDetailsServiceMock.save(any(RegisterAccountDTO.class))).thenReturn(USER_ACCOUNT_1_OUTPUT);

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .post(PUBLIC_REGISTER)
                            .content(json).contentType(MediaType.APPLICATION_JSON)
                    )

                    // then
                    .andDo(print())
                    .andExpect(jsonPath("$.email", org.hamcrest.Matchers.is("user@email.com")))
                    .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("Registration was successful")))
                    .andExpect(status().isCreated());

        }

        @Test
        @DisplayName("Throw if email already registered Test")
        @SneakyThrows
        void emailAlreadyRegisteredThrowTest() {

            // given
            when(jwtUserDetailsServiceMock.save(any(RegisterAccountDTO.class)))
                    .thenThrow(new AccountException(ACCOUNT_EXISTS_EXCEPTION));

            // when
            mockMvc.perform(
                            MockMvcRequestBuilders.post(PUBLIC_REGISTER).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is(ACCOUNT_EXISTS_EXCEPTION)));

        }

    }

    @Nested
    @DisplayName("Method: createAuthenticationToken, Checks generated token authorization")
    class generateTokenAuthenticationTest {

        // given
        private final JwtAuthRequest JWT_USER_AUTH_REQUEST_1 = new JwtAuthRequest("user@email.com", "123456");
        private final UserDetails USER_DETAILS_1 = new JwtUserDetails(USER_ACCOUNT_1_OUTPUT);
        String jsonString = gson.toJson(JWT_USER_AUTH_REQUEST_1);

        @Test
        @DisplayName("User gets Authentication Token Success test")
        @WithMockUser(username = "user@email.com")
        @SneakyThrows
        void existingUserCanGetTokenAndAuthenticationTest() {

            // given
            when(jwtUserDetailsServiceMock.loadUserByUsername(JWT_USER_AUTH_REQUEST_1.getEmail()))
                    .thenReturn(USER_DETAILS_1);
            when(jwtTokenUtilMock.generateToken(USER_DETAILS_1.getUsername())).thenReturn("TestGeneratedToken");

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .post(PUBLIC_AUTHENTICATE).contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString)
                    )

                    // then
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token", org.hamcrest.Matchers.is("TestGeneratedToken")))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Anonymous not allowed for Authentication Token Failure")
        @WithAnonymousUser
        @SneakyThrows
        void anonymousNotAllowedToGetTokenTest() {

            // given
            doThrow(UsernameNotFoundException.class).when(jwtUserDetailsServiceMock).validatePassword(any(JwtAuthRequest.class));


            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .post(PUBLIC_AUTHENTICATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

            // then
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(unauthenticated());
        }
    }

    @Nested
    @DisplayName("Testing accessing User and Admin site")
    class accessingUserAndAdminSiteTest {

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous not allowed to access User site Test")
        @SneakyThrows
        void anonymousNotAllowedToAccessUserSiteTest() {

            // when
            mockMvc.perform(MockMvcRequestBuilders.get(RESTRICTED_SITE_USER)).
                    andDo(print()
                    )

                    // then
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason(org.hamcrest.Matchers.is(ACCESS_DENIED_EXCEPTION)))
                    .andExpect(unauthenticated());

        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous not allowed to access Admin Site Test")
        @SneakyThrows
        void anonymousNotAllowedToAccessAdminSiteTest() {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .get(RESTRICTED_SITE_ADMIN)
                    )

                    // then
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason(org.hamcrest.Matchers.is(ACCESS_DENIED_EXCEPTION)))
                    .andExpect(unauthenticated());
        }

        @Test
        @WithMockUser(username = "user@email.com")
        @DisplayName("User not allowed to access Admin Site Test")
        @SneakyThrows
        void userNotAllowedToAccessAdminSiteTest() {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .get(RESTRICTED_SITE_ADMIN)
                    )

                    // then
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is(ACCESS_DENIED_EXPLANATION_EXCEPTION)))
                    .andExpect(authenticated());
        }

        @Test
        @WithMockUser(username = "user@email.com")
        @DisplayName("User allowed to access User Site Test")
        @SneakyThrows
        void userAllowedToAccessUserSiteTest() {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .get(RESTRICTED_SITE_USER)
                    )

                    // then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                    .andExpect(content().string(USER_PAGE_RESPONSE))
                    .andExpect(authenticated());
        }

        @Test
        @WithMockUser(username = "admin@email.com", roles = {"USER", "ADMIN"})
        @DisplayName("Admin allowed to access User Site Test")
        @SneakyThrows
        void adminAllowedToAccessUserSiteTest() {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .get(RESTRICTED_SITE_USER)
                    )

                    // then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                    .andExpect(content().string(USER_PAGE_RESPONSE))
                    .andExpect(authenticated());
        }

        @Test
        @WithMockUser(username = "admin@email.com", roles = {"USER", "ADMIN"})
        @DisplayName("Admin allowed to access Admin Site Test")
        @SneakyThrows
        void adminAllowedToAccessAdminSiteTest() {

            // when
            mockMvc.perform(MockMvcRequestBuilders
                            .get(RESTRICTED_SITE_ADMIN)
                    )

                    // then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                    .andExpect(content().string(ADMIN_PAGE_RESPONSE))
                    .andExpect(authenticated());
        }
    }
}

class ResponseHelper {
     static final String PUBLIC_REGISTER = "/shop/api/auth/register";
     static final String PUBLIC_AUTHENTICATE = "/shop/api/auth";
     static final String RESTRICTED_SITE_USER = "/shop/api/auth/user";
     static final String RESTRICTED_SITE_ADMIN = "/shop/api/auth/admin";
     static final String USER_PAGE_RESPONSE = "Hello User";
     static final String ADMIN_PAGE_RESPONSE = "Hello Admin";
     static final String ACCOUNT_EXISTS_EXCEPTION = "Account already exists.";
     static final String ACCESS_DENIED_EXCEPTION = "Access Denied";

    static final String ACCESS_DENIED_EXPLANATION_EXCEPTION = "Access Denied. You are not authorized to see content.";
}
