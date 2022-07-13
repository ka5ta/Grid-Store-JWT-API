package com.shop.APIJWTStore.dto;

import java.io.Serializable;

public class JwtAuthRequest implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;

    private String email;
    private String password;

    //need default constructor for JSON Parsing
    public JwtAuthRequest()
    {}

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
