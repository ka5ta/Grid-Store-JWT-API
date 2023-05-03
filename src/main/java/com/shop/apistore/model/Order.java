package com.shop.apistore.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name="Orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private LocalDate orderDate;
    private Boolean status;
    @OneToOne
    private Basket basket;

    public Order(Account account, Basket basket) {
        this.account = account;
        this.basket = basket;
        this.orderDate = LocalDate.now();
        this.status = true;
    }

}
