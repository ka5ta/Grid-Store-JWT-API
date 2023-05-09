package com.shop.apistore.service;

import com.shop.apistore.config.CustomPasswordEncoderConfig;
import com.shop.apistore.dto.ChangePasswordDTO;
import com.shop.apistore.model.Account;
import com.shop.apistore.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    private final CustomPasswordEncoderConfig encoderConfig;


    public Account save(Account account) throws AccountException {

        Optional<Account> byEmailIgnoreCase = repository.findByEmailIgnoreCase(account.getEmail());

        if (byEmailIgnoreCase.isPresent()) {
            throw new AccountException("Account already exists.");
        }

        String password = encoderConfig.encodePassword(account.getPassword());
        account.setPassword(password);

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
        List<Account> allAccounts = new ArrayList<>();

        Iterable<Account> allAccountsIterable = repository.findAll();
        allAccountsIterable.forEach(allAccounts::add);

        return allAccounts;
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO, Principal principal) throws AccountException {
        String oldPassword = changePasswordDTO.getOldPassword();
        String newPassword = changePasswordDTO.getNewPassword();
        String email = principal.getName();

        Account account = findByEmailOrThrow(email);
        if (comparePasswords(oldPassword, account.getPassword())) {
            account.setPassword(encoderConfig.encodePassword(newPassword));
            repository.save(account);
            log.info("New Password created");
        } else {
            throw new AccountException("Old password doesn't match. Password was not changed.");
        }
    }

    public boolean comparePasswords(String requestPassword, String encodedPassword) {
        if (!encoderConfig.comparePasswords(requestPassword, encodedPassword)) {
            throw new BadCredentialsException("Password is wrong");
        }
        return true;
    }
}
