package com.shop.apistore.dto;

import java.io.Serial;
import java.io.Serializable;

public class SuccessResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
