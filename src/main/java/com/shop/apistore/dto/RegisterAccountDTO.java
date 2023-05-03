package com.shop.apistore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterAccountDTO {
    private String email;
    private String password;

    private String role;

    public RegisterAccountDTO() {
    }

    public RegisterAccountDTO(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
