package com.shop.APIJWTStore.controller;

import com.shop.APIJWTStore.dto.ChangePasswordDTO;
import com.shop.APIJWTStore.dto.ErrorResponseDTO;
import com.shop.APIJWTStore.dto.SuccessResponseDTO;
import com.shop.APIJWTStore.model.Account;
import com.shop.APIJWTStore.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountException;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @Secured("ROLE_ADMIN")
    @GetMapping("/shop/api/auth/all")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountService.getAllAccounts());
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PostMapping("/shop/api/auth/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, Principal principal) {

        try {
            accountService.changePassword(changePasswordDTO, principal);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SuccessResponseDTO("Password was changed."));
        } catch(AccountException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponseDTO(e.getMessage()));
        }

    }
}
