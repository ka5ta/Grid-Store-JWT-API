package com.shop.apistore.dto;

import java.io.Serial;
import java.io.Serializable;

public class JwtAuthResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String jwttoken;

    public JwtAuthResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
