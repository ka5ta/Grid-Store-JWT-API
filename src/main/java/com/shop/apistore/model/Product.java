package com.shop.apistore.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String brand;
    private double price;
    private int stock;

    public Product(String brand, double price, int stock) {
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }


}
