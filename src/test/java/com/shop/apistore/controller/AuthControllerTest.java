package com.shop.apistore.controller;

import com.google.gson.Gson;
import com.shop.apistore.constraint.Role;
import com.shop.apistore.dto.JwtAuthRequest;
import com.shop.apistore.dto.RegisterAccountDTO;
import com.shop.apistore.model.Account;
import com.shop.apistore.model.JwtUserDetails;
import com.shop.apistore.service.JwtUserDetailsService;
import com.shop.apistore.utils.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.security.auth.login.AccountException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//todo why those annotations are working but not @AutoConfigureMockMvc and @WebMvcTest() ?

@DisplayName("Web Controller Functionalities testing")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String PUBLIC_REGISTER = "/shop/api/auth/register";
    private static final String PUBLIC_AUTHENTICATE = "/shop/api/authenticate";
    private static final String RESTRICTED_SITE_USER = "/shop/api/auth/user";
    private static final String RESTRICTED_SITE_ADMIN = "/shop/api/auth/admin";

    private static final String USER_PAGE_RESPONSE = "Hello User";
    private static final String ADMIN_PAGE_RESPONSE = "Hello Admin";
    private static final String USER_ADMIN_PAGE_ERROR_RESPONSE = "Access Denied. No Token ...";
    private static final String ACCOUNT_EXISTS_EXCEPTION = "Account already exists.";
    private static final String ACCESS_DENIED_EXCEPTION = "Access Denied";
    private static final String USER_NOT_FOUND_EXCEPTION = "User not found with email: ";
    private static final String ACCESS_DENIED_EXPLANATION_EXCEPTION = "Access Denied. You are not authorized to see content.";

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JwtUserDetailsService jwtUserDetailsServiceMock;
    @MockBean
    private JwtTokenUtil jwtTokenUtilMock;

    private final Account USER_ACCOUNT_1_OUTPUT = new Account(1L, "user@email.com", passwordEncoder.encode("123456"),
            Role.USER);
    private final Account ADMIN_ACCOUNT_2_OUTPUT = new Account(2L, "admin@email.com", passwordEncoder.encode("654321"),
            Role.ADMIN);

    private final UserDetails USER_DETAILS_1 = new JwtUserDetails(USER_ACCOUNT_1_OUTPUT);
    private final UserDetails ADMIN_DETAILS_2 = new JwtUserDetails(ADMIN_ACCOUNT_2_OUTPUT);

    @Nested
    @DisplayName("Register endpoint Test")
    class registerAuthControllerTest {

        // Before
        final RegisterAccountDTO USER_REGISTER_ACCOUNT_DTO_1 = new RegisterAccountDTO("user@email.com", "123456",
                Role.USER.name());
        Gson gson = new Gson();
        String json = gson.toJson(USER_REGISTER_ACCOUNT_DTO_1);

        @Test
        @DisplayName("Anonymous register success")
        void anonymousCanRegisterTest() throws Exception {

            // Mock
            when(jwtUserDetailsServiceMock.save(any(RegisterAccountDTO.class))).thenReturn(USER_ACCOUNT_1_OUTPUT);

            // Request
            mvc.perform(
                    MockMvcRequestBuilders.post(PUBLIC_REGISTER).content(json).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(jsonPath("$.account.email", org.hamcrest.Matchers.is("user@email.com")))
                    .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("Registration was successful")))
                    .andExpect(status().isCreated());

        }

        @Test
        @DisplayName("Throw if email already registered Test")
        void emailAlreadyRegisteredThrowTest() throws Exception {

            // Mock
            when(jwtUserDetailsServiceMock.save(any(RegisterAccountDTO.class)))
                    .thenThrow(new AccountException(ACCOUNT_EXISTS_EXCEPTION));

            // Request
            mvc.perform(
                    MockMvcRequestBuilders.post(PUBLIC_REGISTER).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andDo(print()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is(ACCOUNT_EXISTS_EXCEPTION)));

        }

    }

    @Nested
    @DisplayName("Method: createAuthenticationToken, Checks generated token authorization")
    class generateTokenAuthenticationTest {

        // Before
        private final JwtAuthRequest JWT_USER_AUTH_REQUEST_1 = new JwtAuthRequest("user@email.com", "123456");
        private final UserDetails USER_DETAILS_1 = new JwtUserDetails(USER_ACCOUNT_1_OUTPUT);
        Gson json = new Gson();
        String jsonString = json.toJson(JWT_USER_AUTH_REQUEST_1);

        @Test
        @DisplayName("User gets Authentication Token Success test")
        void existentUserCanGetTokenAndAuthenticationTest() throws Exception {

            // Mock
            when(jwtUserDetailsServiceMock.loadUserByUsername(JWT_USER_AUTH_REQUEST_1.getEmail()))
                    .thenReturn(USER_DETAILS_1);
            when(jwtTokenUtilMock.generateToken(USER_DETAILS_1.getUsername())).thenReturn("TestGeneratedToken");

            // Request
            mvc.perform(MockMvcRequestBuilders.post(PUBLIC_AUTHENTICATE).contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)).andDo(print()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token", org.hamcrest.Matchers.is("TestGeneratedToken")))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Anonymous not allowed for Authentication Token Failure")
        void anonymousNotAllowedToGetTokenTest() throws Exception {

            // Mock
            when(jwtUserDetailsServiceMock.loadUserByUsername(JWT_USER_AUTH_REQUEST_1.getEmail()))
                    .thenThrow(UsernameNotFoundException.class);

            // Request
            mvc.perform(MockMvcRequestBuilders.post(PUBLIC_AUTHENTICATE).contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)).andDo(print()).andExpect(status().isForbidden()).andExpect(unauthenticated());
        }
    }

    @Nested
    @DisplayName("Testing accessing User and Admin site")
    class accessingUserAndAdminSiteTest {

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous not allowed to access User site Test")
        void anonymousNotAllowedToAccessUserSiteTest() throws Exception {

            // Mock
            mvc.perform(MockMvcRequestBuilders.get(RESTRICTED_SITE_USER)).andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason(org.hamcrest.Matchers.is(ACCESS_DENIED_EXCEPTION)))
                    .andExpect(unauthenticated());

        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous not allowed to access Admin Site Test")
        void anonymousNotAllowedToAccessAdminSiteTest() throws Exception {

            // Mock
            mvc.perform(MockMvcRequestBuilders.get(RESTRICTED_SITE_ADMIN)).andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason(org.hamcrest.Matchers.is(ACCESS_DENIED_EXCEPTION)))
                    .andExpect(unauthenticated());
        }

        @Test
        @WithMockUser(username = "user@email.com", roles = { "USER" })
        @DisplayName("User not allowed to access Admin Site Test")
        void userNotAllowedToAccessAdminSiteTest() throws Exception {

            // Mock
            mvc.perform(MockMvcRequestBuilders.get(RESTRICTED_SITE_ADMIN)).andDo(print())
                    .andExpect(status().isForbidden())
                    // .andExpect( content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is(ACCESS_DENIED_EXPLANATION_EXCEPTION)))
                    .andExpect(authenticated());
        }

        @Test
        @WithMockUser(username = "user@email.com", roles = { "USER" })
        @DisplayName("User allowed to access User Site Test")
        void userAllowedToAccessUserSiteTest() throws Exception {

            // Mock
            mvc.perform(MockMvcRequestBuilders.get(RESTRICTED_SITE_USER)).andDo(print()).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                    .andExpect(content().string(USER_PAGE_RESPONSE)).andExpect(authenticated());
        }

        @Test
        @WithMockUser(username = "admin@email.com", roles = { "USER", "ADMIN" })
        @DisplayName("Admin allowed to access User Site Test")
        void adminAllowedToAccessUserSiteTest() throws Exception {

            // Mock
            mvc.perform(MockMvcRequestBuilders.get(RESTRICTED_SITE_USER)).andDo(print()).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                    .andExpect(content().string(USER_PAGE_RESPONSE)).andExpect(authenticated());
        }

        @Test
        @WithMockUser(username = "admin@email.com", roles = { "USER", "ADMIN" })
        @DisplayName("Admin allowed to access Admin Site Test")
        void adminAllowedToAccessAdminSiteTest() throws Exception {

            // Mock
            mvc.perform(MockMvcRequestBuilders.get(RESTRICTED_SITE_ADMIN)).andDo(print()).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                    .andExpect(content().string(ADMIN_PAGE_RESPONSE)).andExpect(authenticated());
        }
    }
}
