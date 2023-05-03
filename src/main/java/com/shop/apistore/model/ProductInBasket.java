package com.shop.apistore.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class ProductInBasket {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "Id")
    private Product product;

    private int quantityInBasket;

    public ProductInBasket(Product product, int quantity) {
        this.product = product;
        this.quantityInBasket = quantity;
    }
}
