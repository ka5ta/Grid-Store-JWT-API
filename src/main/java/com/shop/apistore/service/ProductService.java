package com.shop.apistore.service;

import com.shop.apistore.error.NoSuchProductException;
import com.shop.apistore.error.NotEnoughQuantity;
import com.shop.apistore.model.Product;
import com.shop.apistore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) throws NoSuchProductException {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else
            throw new NoSuchProductException("Product with id: " + id + ", do not exists.");
    }


    public Product getProduct(Long productId) throws NotEnoughQuantity, NoSuchProductException {
        Product currentProduct = getProductById(productId);
        int stock = currentProduct.getStock();

        if (stock > 0) {
            return currentProduct;
        } else
            throw new NotEnoughQuantity("Not enough product quantity in stock to complete this operation.");
    }


}
