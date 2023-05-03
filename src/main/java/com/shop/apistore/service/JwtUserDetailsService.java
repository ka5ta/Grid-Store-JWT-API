package com.shop.apistore.service;

import com.shop.apistore.dto.JwtAuthRequest;
import com.shop.apistore.dto.RegisterAccountDTO;
import com.shop.apistore.model.Account;
import com.shop.apistore.model.JwtUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        Account account = new Account(registerAccountDTO.getEmail(), registerAccountDTO.getPassword(),
                registerAccountDTO.getRole());

        return accountService.save(account);
    }

    public void validatePassword(JwtAuthRequest authenticationRequest){
        UserDetails userDetails = loadUserByUsername(authenticationRequest.getEmail());
        accountService.comparePasswords(authenticationRequest.getPassword(), userDetails.getPassword());
    }
}
