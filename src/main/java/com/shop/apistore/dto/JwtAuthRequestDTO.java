package com.shop.apistore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtAuthRequestDTO {

    private String email;
    private String password;
}
