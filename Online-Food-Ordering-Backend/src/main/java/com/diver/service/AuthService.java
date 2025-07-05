package com.diver.service;

import com.diver.config.JwtProvider;
import com.diver.exception.EmailAlreadyExistsException;
import com.diver.model.Cart;
import com.diver.model.USER_ROLE;
import com.diver.model.User;
import com.diver.repository.CartRepository;
import com.diver.repository.UserRepository;
import com.diver.request.LoginRequest;
import com.diver.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Servicio que encapsula toda la lógica de negocio para la autenticación,
 * incluyendo el registro de nuevos usuarios y el inicio de sesión.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Realiza la validación de email, encripta la contraseña, guarda el nuevo usuario,
     * le crea un carrito asociado y genera un token JWT inicial.
     * Toda la operación es transaccional, garantizando la consistencia de los datos.
     *
     * @param requestUser El objeto User con los datos del formulario de registro.
     * @return Un objeto {@link AuthResponse} con el token JWT y un mensaje de éxito.
     * @throws EmailAlreadyExistsException si el email ya está en uso.
     */
    @Transactional
    public AuthResponse registerUser(User requestUser) {
        // 1. Validar que el email no exista
        userRepository.findByEmail(requestUser.getEmail()).ifPresent(user -> {
            throw new EmailAlreadyExistsException("Este email ya está en uso: " + requestUser.getEmail());
        });

        // 2. Crear y configurar la nueva entidad User
        User newUser = new User();
        newUser.setEmail(requestUser.getEmail());
        newUser.setFullName(requestUser.getFullName());
        newUser.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        newUser.setRole(requestUser.getRole() != null ? requestUser.getRole() : USER_ROLE.ROLE_CUSTOMER);

        User savedUser = userRepository.save(newUser);

        // 3. Crear y asociar un carrito de compras
        Cart cart = new Cart();
        cart.setCustomer(savedUser);
        cartRepository.save(cart);

        // 4. Crear un objeto Authentication para el nuevo usuario
        // Nota: Spring Security no está involucrado aquí, es para la generación del token.
        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());

        // 5. Generar el token
        String token = jwtProvider.generateToken(authentication);

        // 6. Devolver la respuesta
        AuthResponse response = new AuthResponse();
        response.setJwt(token);
        response.setMessage("Usuario registrado exitosamente");
        response.setRole(savedUser.getRole());
        return  response;
    }

    /**
     * Autentica a un usuario existente y le proporciona un nuevo token JWT.
     *
     * @param request El DTO {@link LoginRequest} con el email y la contraseña.
     * @return Un objeto {@link AuthResponse} con el token JWT y un mensaje de éxito.
     * @throws BadCredentialsException si las credenciales son inválidas.
     */
    public AuthResponse loginUser(LoginRequest request) {
        // 1. Autenticar usando el AuthenticationManager de Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Generar el token JWT para la sesión autenticada
        String token = jwtProvider.generateToken(authentication);

        // 3. Obtener el rol para la respuesta
        USER_ROLE role = getRoleFromAuthentication(authentication);

        // 4. Devolver la respuesta
        AuthResponse response = new AuthResponse();
        response.setJwt(token);
        response.setMessage("Inicio de sesión exitoso");
        response.setRole(role);
        return  response;
    }

    /**
     * Método de utilidad para extraer el primer rol de un objeto Authentication.
     */
    private USER_ROLE getRoleFromAuthentication(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .map(USER_ROLE::valueOf)
                .orElse(USER_ROLE.ROLE_CUSTOMER);
    }
}