package com.shop.apistore.service;

import com.shop.apistore.model.Product;
import com.shop.apistore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else
            throw new NoSuchElementException();
    }


    public Product isProductAvailable(Long productId) throws InsufficientResourcesException {
        Product currentProduct = getProductById(productId);
        int stock = currentProduct.getStock();

        if (stock > 0) {
            return currentProduct;
        } else
            throw new InsufficientResourcesException("Not enough products in stock to complete this operation.");
    }


}
