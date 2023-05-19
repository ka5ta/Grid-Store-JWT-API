package com.shop.apistore.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode
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

    @EqualsAndHashCode.Exclude
    private int quantityInBasket;

    public ProductInBasket(Product product, int quantity) {
        this.product = product;
        this.quantityInBasket = quantity;
    }
}
