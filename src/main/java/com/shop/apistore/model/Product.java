package com.shop.apistore.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
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
