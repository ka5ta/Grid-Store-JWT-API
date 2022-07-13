package com.shop.APIJWTStore.dto;

import java.io.Serializable;

public class SuccessResponseDTO implements Serializable {
    private static final long serialVersionUID = 809187909144L;

    private String message;

    public SuccessResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

