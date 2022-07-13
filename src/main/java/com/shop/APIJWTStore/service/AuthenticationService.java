package com.shop.APIJWTStore.service;

import com.shop.APIJWTStore.dto.JwtAuthRequest;
import com.shop.APIJWTStore.model.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;

    public void authenticate(JwtAuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        } catch (DisabledException e) {
            log.warn("Account is disabled");
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials");
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }


}
