package com.shop.apistore.dto;

import java.io.Serial;
import java.io.Serializable;

public class JwtAuthRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    // need default constructor for JSON Parsing
    public JwtAuthRequest() {
    }

    public JwtAuthRequest(String username, String password) {
        this.setEmail(username);
        this.setPassword(password);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
