package com.shop.apistore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterAccountDTO {

    private String email;
    private String password;
    private String role;
}
