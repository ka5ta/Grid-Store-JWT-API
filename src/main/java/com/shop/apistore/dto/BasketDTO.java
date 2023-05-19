package com.shop.apistore.dto;

import com.shop.apistore.model.ProductInBasket;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class BasketDTO {

    private Set<ProductInBasket> productsInBasket;
    private double subtotal;
}
