package com.shop.APIJWTStore.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class JwtUserDetails implements UserDetails {

    private Account account;

    public JwtUserDetails(Account account) {
    this.account = account;
    }

        @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            account.getRoles().forEach(role -> {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+role.name());
                grantedAuthorities.add(grantedAuthority);
            });
            return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
