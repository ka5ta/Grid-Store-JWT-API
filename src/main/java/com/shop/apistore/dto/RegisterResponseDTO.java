package com.shop.apistore.dto;

import com.shop.apistore.constraint.Role;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class RegisterResponseDTO {

    private Long id;
    private String email;
    private Role role;
    private String message;

}
