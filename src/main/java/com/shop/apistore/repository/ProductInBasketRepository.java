package com.shop.apistore.repository;

import com.shop.apistore.model.ProductInBasket;
import org.springframework.data.repository.CrudRepository;

public interface ProductInBasketRepository extends CrudRepository<ProductInBasket, Long> {
}
