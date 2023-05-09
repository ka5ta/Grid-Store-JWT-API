package com.shop.apistore.service;

import com.shop.apistore.dto.BasketDTO;
import com.shop.apistore.model.*;
import com.shop.apistore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final BasketService basketService;
    private final AccountService accountService;
    private final OrderRepository orderRepository;

    @Transactional
    public BasketDTO placeOrder(String email) throws InsufficientResourcesException {
        // Find account and assigned basket
        Account account = accountService.findByEmailOrThrow(email);
        Basket basket = basketService.getBasket(email);
        Set<ProductInBasket> productsInBasket = basket.getProductsInBasket();

        // check if order can be placed
        canPlaceOrderAndUpdateStock(productsInBasket);

        // Save order
        saveOrder(account, basket);

        // Change basket status and update product in stuck
        basket.setOrderPlaced(true);


        double subtotal = basketService.countSubtotal(productsInBasket);
        return new BasketDTO(productsInBasket, subtotal);
    }

    private boolean canPlaceOrderAndUpdateStock(Set<ProductInBasket> productsInBasket) throws InsufficientResourcesException {

        // If basket is empty - order can't be placed
        if (productsInBasket.isEmpty()){
            throw new InsufficientResourcesException("You can't create order, there are no items in your basket");
        }

        List<ProductInBasket> insufficientProducts = productsInBasket.stream().filter(p -> {
                            int stock = p.getProduct().getStock();
                            int quantityInBasket = p.getQuantityInBasket();

                            // update product stock
                            if(stock >= quantityInBasket) {
                                p.getProduct().setStock(stock - quantityInBasket);
                                return false;
                            }
                            return true;
                        }
                )
                .toList();

        if (insufficientProducts.isEmpty()) {
            return true;
        } else {
            throw new InsufficientResourcesException(
                    String.format("The order could not be placed because quantity of below products is not sufficient: %s", insufficientProducts)
            );
        }
    }

    private void saveOrder(Account account, Basket basket) {
        Order order = new Order(account, basket);
        orderRepository.save(order);
    }
}
