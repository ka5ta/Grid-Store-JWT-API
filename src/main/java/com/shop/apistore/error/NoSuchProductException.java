package com.shop.apistore.error;

public class NoSuchProductException extends Exception {
    public NoSuchProductException(String message) {
        super(message);
    }
}
