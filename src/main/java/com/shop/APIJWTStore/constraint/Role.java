package com.shop.APIJWTStore.constraint;

import javax.persistence.Enumerated;

public enum Role {

    USER("USER"), ADMIN("ADMIN");


    private final String value;

    Role(String value) {
        this.value = value;
    }
}
