package com.shop.APIJWTStore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class RegisterResponseDTO implements Serializable {

    private AccountResponseDTO account;
    private String message;
}
