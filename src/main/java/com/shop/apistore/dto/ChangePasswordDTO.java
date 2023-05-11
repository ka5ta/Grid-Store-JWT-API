package com.shop.apistore.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangePasswordDTO {

    private final String oldPassword;
    private final String newPassword;

}
