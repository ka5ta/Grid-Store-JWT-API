package com.shop.apistore.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
public class ProductInBasket implements Comparable<ProductInBasket>{

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

    @Override
    public int compareTo(ProductInBasket pib) {

        String thisProductBrand = this.product.getBrand();
        String otherProductBrand = pib.getProduct().getBrand();

        if (thisProductBrand.compareTo(otherProductBrand) > 0) {
            return 1;
        } else if (thisProductBrand.compareTo(otherProductBrand) < 0) {
            return -1;
        }
        return 0;
    }
}
