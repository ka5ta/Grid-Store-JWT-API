package com.shop.apistore.dto;

import com.shop.apistore.model.ProductInBasket;
import com.shop.apistore.utils.CustomComparatorProduct;
import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

@Data
public class BasketDTO {

    private TreeSet<ProductInBasket> sortedProductsInBasket;
    private double subtotal = 0;

    public BasketDTO(Set<ProductInBasket> productsInBasket, double subtotal) {
        this.sortedProductsInBasket = new TreeSet<>(new CustomComparatorProduct());
        sortedProductsInBasket.addAll(productsInBasket);

        this.subtotal = subtotal;
    }
}
