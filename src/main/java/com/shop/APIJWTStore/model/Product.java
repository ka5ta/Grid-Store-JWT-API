package com.shop.APIJWTStore.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name="products")
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String brand;
    private double price;
    private int stock;

}
