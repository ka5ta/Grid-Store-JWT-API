package com.shop.apistore.utils;

import com.shop.apistore.model.ProductInBasket;

import java.util.Comparator;

public class CustomComparatorProduct implements Comparator<ProductInBasket> {

    @Override
    public int compare(ProductInBasket pib1, ProductInBasket pib2) {

        if (getBrand(pib1).compareTo(getBrand(pib2)) > 0) {
            return 1;
        } else if (getBrand(pib1).compareTo(getBrand(pib2)) < 0) {
            return -1;
        }
        return 0;
    }

    private String getBrand(ProductInBasket pib1) {
        return pib1.getProduct().getBrand();
    }
}

