package com.shop.apistore.repository;

import com.shop.apistore.model.Order;
import org.springframework.data.repository.CrudRepository;


public interface OrderRepository extends CrudRepository<Order, Long> {
}
