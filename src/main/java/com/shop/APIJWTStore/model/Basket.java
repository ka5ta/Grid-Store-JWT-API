package com.shop.APIJWTStore.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "baskets")
public class Basket {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToMany
    @JoinTable(name="products_in_baskets",
            joinColumns = @JoinColumn(name = "basket_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    private int quantity;
}
