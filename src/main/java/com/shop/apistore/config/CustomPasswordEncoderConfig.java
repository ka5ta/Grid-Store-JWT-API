package com.shop.apistore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class CustomPasswordEncoderConfig {


    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public String encodePassword (String password) {
        return bCryptPasswordEncoder().encode(password);
    }

    public boolean comparePasswords(String requestPassword, String encodedPassword) {
        if (!bCryptPasswordEncoder().matches(requestPassword, encodedPassword)) {
            throw new BadCredentialsException("Password is wrong");
        }
        return true;
    }

}
