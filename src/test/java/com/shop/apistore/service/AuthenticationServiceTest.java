package com.shop.apistore.service;

import com.shop.apistore.dto.JwtAuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Functionalities testing")
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManagerMock;

    private AuthenticationService authenticationService;

    private UsernamePasswordAuthenticationToken token;
    private JwtAuthRequest request;

    @BeforeEach
    void setup() {
        String email = "kasia@gmail.com";
        String password = "password";
        authenticationService = new AuthenticationService(authenticationManagerMock);
        token = new UsernamePasswordAuthenticationToken(email, password);
        request = new JwtAuthRequest(email, password);
    }

    @Test
    void authenticateTest_Success() {

        // given
        when(authenticationManagerMock.authenticate(any())).thenReturn(token);

        // when & then
        assertDoesNotThrow(() -> authenticationService.authenticate(request));
    }

    @Test
    void authenticateTest_ThrowsDisabledException() {

        // given
        when(authenticationManagerMock.authenticate(any())).thenThrow(AccessDeniedException.class);

        // when & then
        assertThrows(AccessDeniedException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticateTest_ThrowsBadCredentialsException() {

        // given
        when(authenticationManagerMock.authenticate(any())).thenThrow(BadCredentialsException.class);

        // when & then
        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }
}