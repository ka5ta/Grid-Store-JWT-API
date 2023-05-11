package com.shop.apistore.error;

public class NotEnoughQuantity extends Exception {

    public NotEnoughQuantity(String message) {
        super(message);
    }
}

