package com.shop.apistore.service;

import com.shop.apistore.dto.JwtAuthRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    public void authenticate(JwtAuthRequestDTO authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        } catch (DisabledException e) {
            log.warn("Account is disabled");
            throw new AccessDeniedException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials");
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
