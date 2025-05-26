package com.diver.service;

import com.diver.model.USER_ROLE;
import com.diver.model.User;
import com.diver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ✅ Servicio personalizado de autenticación.
 *
 * Implementa la interfaz `UserDetailsService` de Spring Security.
 * Se encarga de buscar un usuario por email y devolver un objeto `UserDetails` que
 * Spring Security usa para verificar la autenticación y autorización.
 */
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Este método es llamado automáticamente por Spring Security cuando
     * se intenta iniciar sesión (login).
     *
     * @param username puede ser el email del usuario (según implementación)
     * @return UserDetails que contiene email, contraseña y roles
     * @throws UsernameNotFoundException si no se encuentra el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 🔍 Buscar el usuario en la base de datos por su email (username)
        User user = userRepository.findByEmail(username);

        // ⚠️ Validar si el usuario existe
        if (user == null) {
            throw new UsernameNotFoundException("❌ Usuario no encontrado con email: " + username);
        }

        // 🔐 Obtener el rol del usuario (del Enum USER_ROLE)
        USER_ROLE role = user.getRole(); // Ej: CUSTOMER, ADMIN, RESTAURANTE_OWNER

        // 🎫 Crear una lista de autoridades (roles) para el usuario
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Convertir el rol en formato aceptado por Spring Security
        // Ejemplo: USER_ROLE.CUSTOMER → "CUSTOMER"
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        // ✅ Devolver el objeto UserDetails con email, contraseña y rol
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),       // nombre de usuario (email)
                user.getPassword(),    // contraseña encriptada (BCrypt)
                authorities            // lista de roles
        );
    }
}
