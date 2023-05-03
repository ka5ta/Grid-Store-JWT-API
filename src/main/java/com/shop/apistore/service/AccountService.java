package com.shop.apistore.service;

import com.shop.apistore.dto.ChangePasswordDTO;
import com.shop.apistore.model.Account;
import com.shop.apistore.repository.AccountRepository;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
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

        setEncodedPassword(account);
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
        String newPassword = changePasswordDTO.getNewPassword();
        String email = principal.getName();

        Account account = findByEmailOrThrow(email);
        if (passwordEncoder.matches(oldPassword, account.getPassword())) {
            account.setPassword(passwordEncoder.encode(newPassword));
            repository.save(account);
            log.info("New Password created");
        } else {
            throw new AccountException("Old password doesn't match. Password was not changed.");
        }
    }

    public boolean comparePasswords(String requestPassword, String encodedPassword) {
        if (!passwordEncoder.matches(requestPassword, encodedPassword)) {
            throw new BadCredentialsException("Password is wrong");
        }
        return true;
    }

    private void setEncodedPassword(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
    }
}
