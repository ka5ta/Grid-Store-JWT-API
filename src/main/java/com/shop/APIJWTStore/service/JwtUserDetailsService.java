package com.shop.APIJWTStore.service;

import com.shop.APIJWTStore.constraint.Role;
import com.shop.APIJWTStore.dto.RegisterAccountDTO;
import com.shop.APIJWTStore.model.Account;
import com.shop.APIJWTStore.model.JwtUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {


    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new JwtUserDetails(accountService.findByEmailOrThrow(email));
    }

    public Account save(RegisterAccountDTO registerAccountDTO) throws AccountException {
        String email = registerAccountDTO.getEmail();
        String password = registerAccountDTO.getPassword();
        String role = registerAccountDTO.getRole();

        Account account  = accountService.buildAccount(email, password, role);
        return accountService.save(account);
    }
}
