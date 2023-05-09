package com.shop.apistore.sqlrunner;

import com.shop.apistore.model.Account;
import com.shop.apistore.model.Basket;
import com.shop.apistore.model.Product;
import com.shop.apistore.model.ProductInBasket;
import com.shop.apistore.repository.BasketRepository;
import com.shop.apistore.repository.ProductRepository;
import com.shop.apistore.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SQLDataLoader implements ApplicationRunner {

    private final AccountService accountService;
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Add Accounts
        Account adminAccount = new Account("admin@password", "pass-admin", "ADMIN");
        accountService.save(adminAccount);
        Account userAccount = new Account("user@password", "pass-user");
        Account saved = accountService.save(userAccount);

        // Add products
        Product railgun = new Product("Railgun", 133.3, 2);
        productRepository.save(railgun);
        Product nailgun = new Product("Nailgun", 120.22, 10);
        productRepository.save(nailgun);
        Product rocketLuancher = new Product("Rocker Launcher", 500.1, 1);
        productRepository.save(rocketLuancher);
        Product atomBomb = new Product("Atomic-bomb", 1330000, 2);
        productRepository.save(atomBomb);
        Product bomb = new Product("Bomb", 1330000, 2);
        productRepository.save(bomb);

        // Map products to BasketProducts
        ProductInBasket railgunInBasket = new ProductInBasket(railgun, 1);
        ProductInBasket nailgunInBasket = new ProductInBasket(nailgun, 2);

        basketRepository.save(new Basket(
                Stream.of(railgunInBasket, nailgunInBasket).collect(Collectors.toCollection(HashSet::new)), saved));
    }
}
