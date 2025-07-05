package com.diver.service;

import com.diver.dto.AuthenticatedUser;
import com.diver.exception.RestaurantNotFoundException;

import com.diver.model.User;
import com.diver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para cargar los detalles de un usuario para Spring Security.
 * <p>
 * Implementa la interfaz {@link UserDetailsService} y su único propósito es
 * encontrar un usuario por su identificador único (email) y devolverlo.
 * Como nuestra entidad {@code com.diver.model.User} ya implementa {@link UserDetails},
 * podemos devolverla directamente, proporcionando al contexto de seguridad el
 * objeto de usuario completo con todos sus datos.
 */
@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su nombre de usuario (que en nuestro caso es el email).
     * Este método es invocado por Spring Security durante el proceso de autenticación.
     *
     * @param username el email del usuario a buscar.
     * @return un objeto {@link UserDetails} (nuestra propia clase {@code com.diver.model.User}).
     * @throws UsernameNotFoundException si no se encuentra un usuario con el email proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos el usuario por email y lo devolvemos directamente.
        // El método .orElseThrow() de Optional nos permite lanzar una excepción de forma concisa si no se encuentra.
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));



    }
}