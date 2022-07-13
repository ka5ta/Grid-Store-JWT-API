package com.shop.APIJWTStore.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class ErrorResponseDTO implements Serializable {
    private static final long serialVersionUID = 809187909144L;

    private String error;
    private Instant timestamp;


    public ErrorResponseDTO(String error) {
        this.error = error;
        this.timestamp = Instant.now();
    }

}
