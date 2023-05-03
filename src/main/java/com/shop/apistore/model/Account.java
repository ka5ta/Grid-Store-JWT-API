package com.shop.apistore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shop.apistore.constraint.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private Role role;

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.USER;
    }

    public Account(String email, String password, String role) {
        this.email = email;
        this.password = password;
        if (role.equals("ADMIN")) {
            setRole(role);
        } else {
            setRole("USER");
        }
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    private void setRole(String role) {
        this.role = Role.valueOf(role);
    }
}
