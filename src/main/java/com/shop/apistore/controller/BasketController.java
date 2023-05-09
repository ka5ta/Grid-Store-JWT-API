package com.shop.apistore.controller;

import com.shop.apistore.dto.BasketDTO;
import com.shop.apistore.dto.ErrorResponse;

import com.shop.apistore.model.Basket;

import com.shop.apistore.model.ProductInBasket;
import com.shop.apistore.service.BasketService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/api/basket")
public class BasketController {

    private final BasketService basketService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllSortedProductsInBasket(Principal principal) {
        try {
            String email = principal.getName();
            Set<ProductInBasket> allProductsFromBasket = basketService.getAllProductsFromBasket(email);

            double subtotal = basketService.countSubtotal(allProductsFromBasket);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new BasketDTO(allProductsFromBasket, subtotal));

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addProductToBasket(@RequestParam(value = "productId") Long productId, Principal principal) {
        try {
            String email = principal.getName();
            Basket basket = basketService.addProductToBasket(productId, email);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(basket);

        } catch (NoSuchElementException | InsufficientResourcesException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> removeFromBasket(@RequestParam(value = "productId") Long productId,
            Principal principal) {

        try {
            Basket basket = basketService.removeProductFromBasket(productId, principal.getName());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(basket);
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> modifyProductQuantityInBasket(@RequestParam(value = "productId") Long productId,
            @RequestParam(value = "quantity") int newQuantity, Principal principal) {
        try {
            String email = principal.getName();
            Basket basket = basketService.modifyProductInBasket(productId, newQuantity, email);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(basket);
        } catch (NoSuchElementException | InsufficientResourcesException e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @Secured(value = {"ROLE_ADMIN"})
    @GetMapping("/all")
    public ResponseEntity<List<Basket>> showAllBaskets() {
        List<Basket> allBaskets = basketService.getAllBaskets();
        return ResponseEntity.ok(allBaskets);

    }
}
