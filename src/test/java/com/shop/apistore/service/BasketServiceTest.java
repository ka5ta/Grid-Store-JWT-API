package com.shop.apistore.service;

import com.shop.apistore.error.NoSuchProductException;
import com.shop.apistore.error.NotEnoughQuantity;
import com.shop.apistore.model.Account;
import com.shop.apistore.model.Basket;
import com.shop.apistore.model.Product;
import com.shop.apistore.model.ProductInBasket;
import com.shop.apistore.repository.BasketRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("Basket Service Functionalities testing")
class BasketServiceTest {

    private BasketService basketService;
    @Mock
    private AccountService accountServiceMock;
    @Mock
    private BasketRepository basketRepositoryMock;
    @Mock
    private ProductService productServiceMock;


    private Basket basket;
    private Account account;
    private final String email = "kasia@gmail.com";

    private Product nailgun;
    private Product rocket;
    private Product railgun;

    @BeforeEach
    void init() {
        // Initialize service
        basketService = new BasketService(accountServiceMock, basketRepositoryMock, productServiceMock);

        account = new Account(email, "password");

        // Create Product in basket - Nailgun
        ProductInBasket nailgunInBasket = new ProductInBasket();
        nailgunInBasket.setId(1L);

        nailgun = new Product("Nailgun", 300.3, 3);
        nailgun.setId(1L);

        nailgunInBasket.setProduct(nailgun);
        nailgunInBasket.setQuantityInBasket(2);

        // Create Product in basket - Rocket
        ProductInBasket rocketInBasket = new ProductInBasket();
        rocketInBasket.setId(2L);

        rocket = new Product("Rocket", 100.5, 10);
        rocket.setId(2L);

        rocketInBasket.setProduct(rocket);
        rocketInBasket.setQuantityInBasket(5);

        // Add products to basket and assign account
        Set<ProductInBasket> productsInBasket = new HashSet<>();
        productsInBasket.add(nailgunInBasket);
        productsInBasket.add(rocketInBasket);

        basket = new Basket();
        basket.setId(1L);
        basket.setProductsInBasket(productsInBasket);
        basket.setAccount(account);

        railgun = new Product("Railgun", 400.0, 10);
        railgun.setId(3L);
    }

    @BeforeEach
    void given() {
        // given
        when(accountServiceMock.findByEmailOrThrow(email)).thenReturn(account);
        when(basketRepositoryMock.findByAccount(account)).thenReturn(Optional.ofNullable(basket));
    }

    @Test
    @DisplayName("Get all products from basket successful")
    void getAllProductsFromBasketSuccessfulTest() {

        // when
        Set<ProductInBasket> allProductsFromBasket = basketService.getAllProductsFromBasket(email);

        System.out.println(allProductsFromBasket);

        // then
        assertAll(
                () -> assertThat(allProductsFromBasket).hasSize(2),
                () -> assertThat(allProductsFromBasket).anySatisfy(p -> {
                    assertThat(p.getProduct().getBrand()).isEqualTo("Nailgun");
                    assertThat(p.getProduct().getPrice()).isEqualTo(300.3);
                }),
                () -> assertThat(allProductsFromBasket).anySatisfy(p -> {
                            assertThat(p.getProduct().getBrand()).isEqualTo("Rocket");
                            assertThat(p.getProduct().getPrice()).isEqualTo(100.5);
                        }
                )
        );
    }

    @Nested
    @DisplayName("Adding product to basket tests")
    class addProductToBasket {

        @Test
        @SneakyThrows
        @DisplayName("Adding product to basket successful")
        void addProductToBasketSuccessfulTest() {

            // given
            when(productServiceMock.getProduct(3L)).thenReturn(railgun);
            when(basketRepositoryMock.save(basket)).thenReturn(basket);

            // when
            Basket updatedBasket = basketService.addProductToBasket(3L, email);

            // then
            assertAll(
                    () -> assertThat(updatedBasket.getProductsInBasket()).hasSize(3),
                    () -> assertThat(updatedBasket).isEqualTo(basket),
                    () -> assertThat(basket.getProductsInBasket()).anySatisfy(p -> {
                                assertThat(p.getProduct().getBrand()).isEqualTo("Railgun");
                                assertThat(p.getProduct().getPrice()).isEqualTo(400.0);
                            }
                    )
            );

        }

        @Test
        @SneakyThrows
        @DisplayName("Adding existing product to basket changes quantity")
        void addExistingProductToBasketChangesQuantityTest() {

            // given
            when(productServiceMock.getProduct(1L)).thenReturn(nailgun);
            when(basketRepositoryMock.save(basket)).thenReturn(basket);


            // when
            Basket updatedBasket = basketService.addProductToBasket(1L, email);

            // then
            assertAll(
                    () -> assertThat(updatedBasket.getProductsInBasket()).hasSize(2),
                    () -> assertThat(basket.getProductsInBasket()).anySatisfy(p -> {
                                assertThat(p.getProduct().getBrand()).isEqualTo("Nailgun");
                                assertThat(p.getQuantityInBasket()).isEqualTo(3);
                            }
                    )
            );
        }

        @Test
        @SneakyThrows
        @DisplayName("Adding existing product to basket exceeds stock quantity throws exception")
        void addExistingProductToBasketExceedQuantityTest() {

            // given
            when(productServiceMock.getProduct(1L)).thenReturn(nailgun);
            when(basketRepositoryMock.save(basket)).thenReturn(basket);


            // when
            assertDoesNotThrow(() -> basketService.addProductToBasket(1L, email));
            NotEnoughQuantity notEnoughQuantityException = assertThrows(NotEnoughQuantity.class, () -> basketService.addProductToBasket(1L, email));

            // then
            assertThat(notEnoughQuantityException.getMessage()).isEqualTo("Not enough product quantity in stock");
        }

        @Test
        @SneakyThrows
        @DisplayName("Adding product to basket is unsuccessful because product do not exists throws exception")
        void addNewProductToBasketUnsuccessfulTest() {

            // given
            when(productServiceMock.getProduct(10L)).thenThrow(NoSuchProductException.class);


            // when & then
            assertThrows(NoSuchProductException.class, () -> basketService.addProductToBasket(10L, email));
        }
    }

    @Nested
    @DisplayName("Remove product from basket tests")
    class removeProductFromBasketTests {
        @Test
        @SneakyThrows
        @DisplayName("Remove product from basket successful")
        void removeProductFromBasketSuccessfulTest() {

            // given
            when(productServiceMock.getProductById(1L)).thenReturn(nailgun);
            when(basketRepositoryMock.save(any())).thenReturn(basket);


            // when
            Basket updatedBasket = basketService.removeProductFromBasket(1L, email);

            // then
            Set<ProductInBasket> productsInBasket = updatedBasket.getProductsInBasket();
            assertAll(
                    () -> assertThat(productsInBasket).hasSize(1),
                    () -> assertThat(productsInBasket).allSatisfy(p ->
                            assertThat(p.getProduct()).isEqualTo(rocket))
            );
        }

        @Test
        @SneakyThrows
        @DisplayName("Remove product from basket that doesn't exists throws exception")
        void removeProductFromBasketUnsuccessfulTest() {

            // given
            when(productServiceMock.getProductById(3L)).thenReturn(railgun);

            // when
            NoSuchProductException noSuchProductException = assertThrows(NoSuchProductException.class, () -> basketService.removeProductFromBasket(3L, email));

            // then
            assertThat(noSuchProductException.getMessage()).isEqualTo("There is no such product in basket");
        }
    }

    @Nested
    @DisplayName("Modify product quantity in basket tests")
    class modifyProductInBasketTests {
        @Test
        @SneakyThrows
        @DisplayName("Set quantity to 0 Removes product from basket")
        void removeProductFromBasketWhenNewQuantityIsZeroTest() {

            // given
            when(productServiceMock.getProductById(1L)).thenReturn(nailgun);
            when(basketRepositoryMock.save(any())).thenReturn(basket);

            // when
            Basket updatedBasket = basketService.modifyProductInBasket(1L, 0, email);

            // then
            Set<ProductInBasket> productsInBasket = updatedBasket.getProductsInBasket();
            assertAll(
                    () -> assertThat(productsInBasket).hasSize(1),
                    () -> assertThat(productsInBasket).allSatisfy(p ->
                            assertThat(p.getProduct()).isEqualTo(rocket))
            );
        }

        @Test
        @SneakyThrows
        @DisplayName("Increase product quantity in basket successful")
        void increaseProductQuantityInBasketSuccessfulTest() {

            // given
            when(productServiceMock.getProductById(2L)).thenReturn(rocket);
            when(basketRepositoryMock.save(any())).thenReturn(basket);

            // when
            Basket updatedBasket = basketService.modifyProductInBasket(2L, 10, email);

            // then
            Set<ProductInBasket> productsInBasket = updatedBasket.getProductsInBasket();
            assertAll(
                    () -> assertThat(productsInBasket).hasSize(2),
                    () -> assertThat(productsInBasket).anySatisfy(p -> {
                                assertThat(p.getProduct()).isEqualTo(rocket);
                                assertThat(p.getQuantityInBasket()).isEqualTo(10);
                            }
                    )
            );
        }

        @Test
        @SneakyThrows
        @DisplayName("Increase product quantity in basket is unsuccessful because requested quantity is higher than stock")
        void increaseProductQuantityInBasketUnsuccessfulTest() {

            // given
            when(productServiceMock.getProductById(2L)).thenReturn(rocket);

            // when
            NotEnoughQuantity notEnoughQuantity = assertThrows(NotEnoughQuantity.class,
                    () -> basketService.modifyProductInBasket(2L, 20, email));

            // then
            assertThat(notEnoughQuantity.getMessage()).isEqualTo("Not enough product quantity in stock");
        }
    }


}