package com.shop.apistore.controller;

import com.shop.apistore.model.Product;
import com.shop.apistore.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/shop/api/")
public class ProductController {

    private ProductRepository productRepository;

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productRepository.findAll());
    }

}
