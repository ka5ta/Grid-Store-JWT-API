package com.shop.apistore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtAuthResponseDTO {

    @JsonProperty("token")
    private final String jwtToken;

}
