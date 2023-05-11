package com.shop.apistore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "baskets")
public class Basket {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "basket_id", referencedColumnName = "Id")
    @ElementCollection(targetClass = HashSet.class)
    private Set<ProductInBasket> productsInBasket;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private boolean orderPlaced;

    public Basket() {
        this.productsInBasket = new HashSet<>();
    }

    public Basket(Set<ProductInBasket> productsInBasket, Account account) {
        this.productsInBasket = productsInBasket;
        this.account = account;
    }
}
