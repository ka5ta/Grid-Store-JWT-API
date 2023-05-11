package com.shop.apistore.service;

import com.shop.apistore.error.NoSuchProductException;
import com.shop.apistore.error.NotEnoughQuantity;
import com.shop.apistore.model.Account;
import com.shop.apistore.model.Basket;
import com.shop.apistore.model.Product;
import com.shop.apistore.model.ProductInBasket;
import com.shop.apistore.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public Basket addProductToBasket(Long productId, String email) throws NotEnoughQuantity, NoSuchProductException {
        Basket basket = getBasket(email);

        Product productById = productService.getProduct(productId);
        addProduct(basket, productById);
        return saveBasket(basket);
    }

    private Basket addProduct(Basket basket, Product product) throws NotEnoughQuantity {

        try {
            return incrementProductQuantityInBasket(basket, product);

        } catch (NoSuchProductException e) {
            ProductInBasket newProductToBasket = new ProductInBasket(product, 1);
            basket.getProductsInBasket().add(newProductToBasket);
            return basket;
        }
    }

    private Basket incrementProductQuantityInBasket(Basket basket, Product product) throws NotEnoughQuantity, NoSuchProductException {
        ProductInBasket productInBasketToUpdate = findProductInBasket(basket, product);

        if (product.getStock() <= productInBasketToUpdate.getQuantityInBasket()) {
            throw new NotEnoughQuantity("Not enough product quantity in stock");
        }

        // increase quantity by 1
        int updatedQuantity = productInBasketToUpdate.getQuantityInBasket() + 1;
        // update quantity in basket
        productInBasketToUpdate.setQuantityInBasket(updatedQuantity);
        return basket;
    }


    private ProductInBasket findProductInBasket(Basket basket, Product product) throws NoSuchProductException {
        Set<ProductInBasket> productsInBasket = basket.getProductsInBasket();

        return productsInBasket.stream()
                .filter(pib -> pib.getProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new NoSuchProductException("There is no such product in basket"));
    }

    public Basket removeProductFromBasket(Long productId, String email) throws NoSuchProductException {
        Basket basket = getBasket(email);
        Product product = productService.getProductById(productId);

        removeProduct(basket, product);
        return saveBasket(basket);
    }

    private void removeProduct(Basket basket, Product product) throws NoSuchProductException {
        ProductInBasket productInBasketToRemove = findProductInBasket(basket, product);

        basket.getProductsInBasket().remove(productInBasketToRemove);
    }

    public Basket modifyProductInBasket(Long productId, int newQuantity, String email) throws NoSuchProductException, NotEnoughQuantity {
        Basket basket = getBasket(email);
        Product product = productService.getProductById(productId);

        Basket updatedBasket = setProductQuantityInBasket(basket, product, newQuantity);

        return saveBasket(updatedBasket);
    }

    public Basket saveBasket(Basket basket) {
        return basketRepository.save(basket);
    }

    private Basket setProductQuantityInBasket(Basket basket, Product product, int newQuantity) throws NotEnoughQuantity, NoSuchProductException {

        ProductInBasket productInBasketToUpdate = findProductInBasket(basket, product);

        if (product.getStock() < newQuantity) {
            throw new NotEnoughQuantity("Not enough product quantity in stock");
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
