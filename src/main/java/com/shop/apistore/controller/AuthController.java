package com.shop.apistore.controller;

import com.shop.apistore.dto.*;
import com.shop.apistore.model.Account;
import com.shop.apistore.service.JwtUserDetailsService;
import com.shop.apistore.utils.JwtTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/shop/api")
public class AuthController {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/auth/register")
    public ResponseEntity<Object> register(@RequestBody RegisterAccountDTO registerAccountDTO) {
        try {

            Account savedAccount = jwtUserDetailsService.save(registerAccountDTO);

            RegisterResponseDTO response = RegisterResponseDTO.builder()
                    .id(savedAccount.getId())
                    .email(savedAccount.getEmail())
                    .role(savedAccount.getRole())
                    .message("Registration was successful")
                    .build();

            log.info("Registration was successful for email: " + response.getEmail());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (AccountException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/auth")
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtAuthRequest authenticationRequest) {

        try {
            jwtUserDetailsService.validatePassword(authenticationRequest);
            final String token = jwtTokenUtil.generateToken(authenticationRequest.getEmail());
            log.info("token was generated: " + token);

            return ResponseEntity.ok(new JwtAuthResponse(token));

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/auth/user")
    public String helloUser() {
        return "Hello User";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/auth/admin")
    public String helloAdmin() {
        return "Hello Admin";
    }

}

// https://stackoverflow.com/questions/54339794/how-to-get-claims-value-from-jwt-token-authentication
// https://jwt.io/introduction