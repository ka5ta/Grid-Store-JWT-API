package com.shop.apistore.service;

import com.shop.apistore.model.Account;
import com.shop.apistore.model.Basket;
import com.shop.apistore.model.Product;
import com.shop.apistore.model.ProductInBasket;
import com.shop.apistore.repository.BasketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.InsufficientResourcesException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Basket Service Functionalities testing")
class BasketServiceTest {

    @Mock
    private AccountService accountServiceMock;
    @Mock
    private BasketRepository basketRepositoryMock;
    @Mock
    private ProductService productServiceMock;

    private BasketService basketService;

    private Basket basket;
    private Account account;
    private final String email = "kasia@gmail.com";

    @BeforeEach
    void setup() {
        basketService = new BasketService(accountServiceMock, basketRepositoryMock, productServiceMock);
        account = new Account(email, "password");

        ProductInBasket productInBasket = new ProductInBasket();
        productInBasket.setId(1L);
        productInBasket.setQuantityInBasket(2);

        Product product = new Product("Nailgun", 300.3, 3);
        product.setId(1L);

        productInBasket.setProduct(product);

        Set<ProductInBasket> productsInBasket = new HashSet<>();
        productsInBasket.add(productInBasket);

        basket = new Basket();
        basket.setProductsInBasket(productsInBasket);
        basket.setAccount(account);
    }

    @Test
    void getAllProductsFromBasket() {

        // given
        when(accountServiceMock.findByEmailOrThrow(email)).thenReturn(account);
        when(basketRepositoryMock.findByAccount(account)).thenReturn(Optional.ofNullable(basket));

        // when
        Set<ProductInBasket> allProductsFromBasket = basketService.getAllProductsFromBasket(email);

        // then
        assertThat(allProductsFromBasket).hasSize(1);
    }

    @Test
    void addProductToBasket() throws InsufficientResourcesException {

        Product prod = new Product("Railgun", 400.0, 10);
        prod.setId(2L);
        Basket newbasket = new Basket();
        newbasket.setProductsInBasket(basket.getProductsInBasket());
        newbasket.setAccount(account);

        // given
        when(accountServiceMock.findByEmailOrThrow(email)).thenReturn(account);
        when(productServiceMock.getProductById(2L)).thenReturn(prod);
        when(basketRepositoryMock.save(basket)).thenReturn(newbasket);
        when(basketRepositoryMock.findByAccount(account)).thenReturn(Optional.ofNullable(basket));

        // when
        Basket updatedBasket = basketService.addProductToBasket(2L, email);

        // then
        assertThat(updatedBasket.getProductsInBasket()).hasSize(2);
    }
}