package com.shop.apistore.repository;

import com.shop.apistore.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findAll();
}
