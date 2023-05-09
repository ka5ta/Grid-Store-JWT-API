package com.shop.apistore.service;

import com.shop.apistore.model.Account;
import com.shop.apistore.model.Basket;
import com.shop.apistore.model.Product;
import com.shop.apistore.model.ProductInBasket;
import com.shop.apistore.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final AccountService accountService;
    private final BasketRepository basketRepository;

    private final ProductService productService;

    public Set<ProductInBasket> getAllProductsFromBasket(String email) {
        Basket basket = getBasket(email);
        return basket.getProductsInBasket();
    }

    public Basket addProductToBasket(Long productId, String email) throws InsufficientResourcesException {
        Basket basket = getBasket(email);

        try {
            Product productById = productService.isProductAvailable(productId);
            addProduct(basket, productById);
            return saveBasket(basket);

        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Product with id: " + productId + ", do not exists.");
        }
    }

    private Basket addProduct(Basket basket, Product product) throws InsufficientResourcesException {

        try {
            return updateProductQuantityInBasket(basket, product);

        } catch (NoSuchElementException e) {
            ProductInBasket newProductToBasket = new ProductInBasket(product, 1);
            basket.getProductsInBasket().add(newProductToBasket);
            return basket;
        }
    }

    private Basket updateProductQuantityInBasket(Basket basket, Product product) throws InsufficientResourcesException {
        ProductInBasket productInBasketToUpdate = findProductInBasket(basket, product);

        if (product.getStock() <= productInBasketToUpdate.getQuantityInBasket()) {
            throw new InsufficientResourcesException("Not enough product quantity in stock");
        }

        // increase quantity by 1
        int updatedQuantity = productInBasketToUpdate.getQuantityInBasket() + 1;
        // update quantity in basket
        productInBasketToUpdate.setQuantityInBasket(updatedQuantity);
        return basket;
    }


    private ProductInBasket findProductInBasket(Basket basket, Product product) {
        Set<ProductInBasket> productsInBasket = basket.getProductsInBasket();

        return productsInBasket.stream()
                .filter(pib -> pib.getProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("There is no such product in basket"));
    }

    public Basket removeProductFromBasket(Long productId, String email) {
        Basket basket = getBasket(email);
        Product product = productService.getProductById(productId);

        removeProduct(basket, product);
        return saveBasket(basket);
    }

    private void removeProduct(Basket basket, Product product) {
        ProductInBasket productInBasketToRemove = findProductInBasket(basket, product);

        basket.getProductsInBasket().remove(productInBasketToRemove);
    }

    public Basket modifyProductInBasket(Long productId, int newQuantity, String email) throws InsufficientResourcesException {
        Basket basket = getBasket(email);
        Product product = productService.getProductById(productId);

        return updateProductQuantityInBasket(basket, product, newQuantity);
    }

    public Basket saveBasket(Basket basket) {
        return basketRepository.save(basket);
    }

    private Basket updateProductQuantityInBasket(Basket basket, Product product, int newQuantity) throws InsufficientResourcesException {

        ProductInBasket productInBasketToUpdate = findProductInBasket(basket, product);

        if (product.getStock() < newQuantity) {
            throw new InsufficientResourcesException("Not enough product quantity in stock");
        } else if (newQuantity == 0) {
            removeProduct(basket, product);
            return basket;
        }

        productInBasketToUpdate.setQuantityInBasket(newQuantity);

        return basket;
    }

    private Basket createBasketForAccount(Account account) {
        Basket newBasket = new Basket();
        newBasket.setAccount(accountService.findByEmailOrThrow(account.getEmail()));
        return newBasket;
    }

    public Basket getBasket(String email) {
        Account account = accountService.findByEmailOrThrow(email);
        Optional<Basket> optionalBasket = basketRepository.findByAccount(account);

        return optionalBasket.orElseGet(() -> createBasketForAccount(account));
    }

    public List<Basket> getAllBaskets() {
        return basketRepository.findAll();
    }

    public double countSubtotal(Set<ProductInBasket> productsInBasket) {
        return productsInBasket.stream().mapToDouble(pib -> {
            double price = pib.getProduct().getPrice();
            int quantity = pib.getQuantityInBasket();
            return price * quantity;
        }).sum();
    }

}
