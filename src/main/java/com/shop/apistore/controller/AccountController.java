package com.shop.apistore.controller;

import com.shop.apistore.dto.ChangePasswordDTO;
import com.shop.apistore.dto.ErrorResponse;
import com.shop.apistore.dto.SuccessResponseDTO;
import com.shop.apistore.model.Account;
import com.shop.apistore.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;
import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/shop/api/account")
public class AccountController {

    private final AccountService accountService;

    @Secured("ROLE_ADMIN")
    @GetMapping("all")
    @ResponseBody
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> allAccounts = accountService.getAllAccounts();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(allAccounts);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PostMapping("change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, Principal principal) {

        try {
            accountService.changePassword(changePasswordDTO, principal);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SuccessResponseDTO("Password was changed."));
        } catch (AccountException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }

    }
}
