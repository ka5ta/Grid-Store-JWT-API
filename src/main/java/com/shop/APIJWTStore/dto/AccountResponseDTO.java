package com.shop.APIJWTStore.dto;

import com.shop.APIJWTStore.model.Account;
import com.shop.APIJWTStore.constraint.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class AccountResponseDTO {

    private Long id;
    private String email;
    private Set<Role> roles;

    public AccountResponseDTO(Account account){
        this.id = account.getId();
        this.email = account.getEmail();
        this.roles = account.getRoles();
    }
}
