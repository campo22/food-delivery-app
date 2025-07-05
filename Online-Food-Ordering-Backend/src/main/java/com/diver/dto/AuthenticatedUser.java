package com.diver.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public record AuthenticatedUser(
        Long id,
        String email,
        Collection<? extends GrantedAuthority> authorities) implements UserDetails {


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        // No almacenamos la contraseña en el Principal
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    // El resto de los métodos de UserDetails pueden devolver true
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
