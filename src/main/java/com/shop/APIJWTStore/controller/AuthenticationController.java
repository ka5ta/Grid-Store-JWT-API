package com.shop.APIJWTStore.controller;

import com.shop.APIJWTStore.dto.*;
import com.shop.APIJWTStore.failureHandler.JsonErrorResponseHandler;
import com.shop.APIJWTStore.model.Account;
import com.shop.APIJWTStore.service.JwtUserDetailsService;
import com.shop.APIJWTStore.utils.JwtTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/shop/api")
public class AuthenticationController {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;



    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterAccountDTO registerAccountDTO) {
        try {
            Account savedAccount = jwtUserDetailsService.save(registerAccountDTO);
            AccountResponseDTO responseDTO = new AccountResponseDTO(savedAccount);
            RegisterResponseDTO dto = new RegisterResponseDTO(responseDTO, "Registration was successful");
            log.info("Registration was successful for email: " + responseDTO.getEmail());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto);

        } catch (AccountException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(JsonErrorResponseHandler.createJsonErrorMessage(e.getMessage()));
        }
    }


    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthRequest authenticationRequest) {

        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());

        final String token = jwtTokenUtil.generateToken(userDetails.getUsername());
        log.info("token was generated: " + token);

        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody Account account) {
        jwtTokenUtil.invalidateTokens(account);
        return new ResponseEntity<>(HttpStatus.OK);
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

//https://stackoverflow.com/questions/54339794/how-to-get-claims-value-from-jwt-token-authentication
// https://jwt.io/introduction