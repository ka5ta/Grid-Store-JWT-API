package com.shop.apistore.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class ErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String error;
    private final Instant timestamp;

    public ErrorResponse(String error) {
        this.error = error;
        this.timestamp = Instant.now();
    }

    public String getError() {
        return error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
