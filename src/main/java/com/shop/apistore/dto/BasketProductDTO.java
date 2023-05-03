package com.shop.apistore.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
public class BasketProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long productId;
    private String brand;
    private double price;
    private int quantityInBasket;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BasketProductDTO that = (BasketProductDTO) o;
        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
