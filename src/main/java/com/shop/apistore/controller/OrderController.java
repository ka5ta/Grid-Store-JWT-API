package com.shop.apistore.controller;

import com.shop.apistore.dto.BasketDTO;
import com.shop.apistore.dto.ErrorResponse;
import com.shop.apistore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.naming.InsufficientResourcesException;
import java.security.Principal;

@Controller
@RequestMapping("/shop/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("checkout")
    public ResponseEntity<Object> createOrder(Principal principal) {
        String email = principal.getName();
        try {
            BasketDTO basketDTO = orderService.placeOrder(email);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(basketDTO);
        } catch (InsufficientResourcesException e){
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
