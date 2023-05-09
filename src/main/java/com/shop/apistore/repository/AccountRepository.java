package com.shop.apistore.repository;

import com.shop.apistore.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByEmailIgnoreCase(String email);
}
