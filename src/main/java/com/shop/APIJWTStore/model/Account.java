package com.shop.APIJWTStore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shop.APIJWTStore.constraint.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Data
@Table(name="accounts")
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @ElementCollection(targetClass= Role.class, fetch = FetchType.EAGER)
    private Set<Role> roles;

    public Account() {
        this.roles = new HashSet<>();
        roles.add(Role.USER);
    }

    public Account(String email, String password) {
        this();
        this.email = email;
        this.password = password;
    }

    public Account(String email, String password, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }


    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role){
        Role newRole = Role.valueOf(role);
        if(!this.roles.contains(newRole)){
            this.roles.add(newRole);
        }
    }

}
