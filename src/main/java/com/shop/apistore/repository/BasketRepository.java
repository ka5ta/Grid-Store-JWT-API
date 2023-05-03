package com.shop.apistore.repository;

import com.shop.apistore.model.Account;
import com.shop.apistore.model.Basket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepository extends CrudRepository<Basket, Long> {

    @Query(value = "select b from Basket b where b.account = :account and b.orderPlaced = false")
    Optional<Basket> findByAccount(@Param("account") Account account);

    List<Basket> findAll();
}
