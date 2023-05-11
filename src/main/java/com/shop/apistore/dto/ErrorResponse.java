package com.shop.apistore.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String error;
    private final Instant timestamp = Instant.now();

}
