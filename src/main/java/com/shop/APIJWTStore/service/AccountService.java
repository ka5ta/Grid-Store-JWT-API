package com.shop.APIJWTStore.service;

import com.shop.APIJWTStore.dto.ChangePasswordDTO;
import com.shop.APIJWTStore.dto.SuccessResponseDTO;
import com.shop.APIJWTStore.model.Account;
import com.shop.APIJWTStore.repository.AccountRepository;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.security.auth.login.AccountException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Account save(Account account) throws AccountException {

        Optional<Account> byEmailIgnoreCase = repository.findByEmailIgnoreCase(account.getEmail());

        if (byEmailIgnoreCase.isPresent()) {
            throw new AccountException("Account already exists.");
        }

        return repository.save(account);
    }

    public Account findByEmailOrThrow(String email) {
        Optional<Account> byEmailIgnoreCase = repository.findByEmailIgnoreCase(email);

        if (byEmailIgnoreCase.isPresent()) {
            return byEmailIgnoreCase.get();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);

    }

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO, Principal principal) throws AccountException {
        String oldPassword = changePasswordDTO.getOldPassword();
        log.info("old Password: " + oldPassword);
        String newPassword = changePasswordDTO.getNewPassword();
        String email = principal.getName();

        Account account = findByEmailOrThrow(email);
        if(passwordEncoder.matches(oldPassword, account.getPassword())){
            account.setPassword(passwordEncoder.encode(newPassword));
            repository.save(account);
            log.info("New Password: " + newPassword);
        } else {
            throw new AccountException("Old password doesn't match. Password was not changed.");
        }

    }

    public Account buildAccount(String email, String password, String role) {

        Account account = new Account();
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(password));

        if(role.equals("ADMIN")){
            account.setRole(role);
        }

        return account;
    }
}
