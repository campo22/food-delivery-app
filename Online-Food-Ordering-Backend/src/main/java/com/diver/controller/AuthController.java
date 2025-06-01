package com.diver.controller;

import com.diver.config.JwtProvider;
import com.diver.model.Cart;
import com.diver.model.USER_ROLE;
import com.diver.model.User;
import com.diver.repository.CartRepository;
import com.diver.repository.UserRepository;
import com.diver.request.LoginRequest;
import com.diver.response.AuthResponse;
import com.diver.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
   private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CartRepository cartRepository;

 @PostMapping("/signup")
 public ResponseEntity<AuthResponse> createUserHandle(@RequestBody User user) throws Exception {

  // 1️⃣ Verificar si el email ya está registrado
  User isEmailExist = userRepository.findByEmail(user.getEmail());
  if (isEmailExist != null) {
   throw new Exception("Este email ya existe en el sistema");
  }

  // 2️⃣ Crear el usuario y asignar los campos necesarios
  User createdUser = new User();
  createdUser.setEmail(user.getEmail());
  createdUser.setFullName(user.getFullName());
  createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

  // ✅ 3️⃣ Asignar el rol recibido en la petición
  createdUser.setRole(user.getRole()); // Esto es clave

  // 4️⃣ Guardar el usuario
  User savedUser = userRepository.save(createdUser);

  // 5️⃣ Crear carrito vacío asociado al usuario
  Cart cart = new Cart();
  cart.setCustomer(savedUser);
  cartRepository.save(cart);

  // 6️⃣ Autenticar al usuario recién creado
  Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
  );

  // 7️⃣ Registrar al usuario como autenticado en el contexto de Spring
  SecurityContextHolder.getContext().setAuthentication(authentication);

  // 8️⃣ Generar el token JWT
  String jwt = jwtProvider.generateToken(authentication);

  // 9️⃣ Preparar la respuesta
  AuthResponse authResponse = new AuthResponse();
  authResponse.setJwt(jwt);
  authResponse.setMessage("¡Usuario creado exitosamente!");
  authResponse.setRole(savedUser.getRole());

  return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
 }



 @PostMapping("/signin")
 public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {

  // 1️⃣ Autenticar al usuario con Spring Security
  Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
  );

  // 2️⃣ Registrar autenticación en el contexto de Spring
  SecurityContextHolder.getContext().setAuthentication(authentication);

  // 3️⃣ Obtener el rol (primer authority de la lista)
  Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
  String role = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

  // 4️⃣ Generar el token JWT
  String jwt = jwtProvider.generateToken(authentication);

  // 5️⃣ Preparar la respuesta
  AuthResponse authResponse = new AuthResponse();
  authResponse.setJwt(jwt);
  authResponse.setMessage("Autenticación exitosa");
  authResponse.setRole(USER_ROLE.valueOf(role)); // ✅ Extrae solo "CUSTOMER", "ADMIN", etc.

  return new ResponseEntity<>(authResponse, HttpStatus.OK);
 }



}
