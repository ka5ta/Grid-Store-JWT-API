package com.shop.apistore.dto;

import com.shop.apistore.constraint.Role;
import com.shop.apistore.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Getter
public class RegisterResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private Role role;
    private String message;

    public RegisterResponseDTO(Account account, String message) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.role = account.getRole();
        this.message = message;
    }
}
