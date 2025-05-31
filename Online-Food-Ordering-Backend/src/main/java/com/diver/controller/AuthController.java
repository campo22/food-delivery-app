package com.diver.controller;

import com.diver.config.JwtProvider;
import com.diver.model.Cart;
import com.diver.model.User;
import com.diver.repository.CartRepository;
import com.diver.repository.UserRepository;
import com.diver.response.AuthResponse;
import com.diver.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

 @PostMapping ("/register")
 public ResponseEntity<AuthResponse> createUserHandle(@RequestBody User user) throws Exception {

  // 1️⃣ Verificar si el email ya existe en la base de datos
  User isEmailExist = userRepository.findByEmail(user.getEmail());
  if (isEmailExist != null) {
   throw new Exception("Este email ya existe en el sistema");
  }

  // 2️⃣ Crear nuevo usuario y encriptar la contraseña
  User createdUser = new User();
  createdUser.setEmail(user.getEmail());
  createdUser.setFullName(user.getFullName());
  createdUser.setPassword(passwordEncoder.encode(user.getPassword()));



  // 3️⃣ Guardar el usuario en la base de datos
  User savedUser = userRepository.save(createdUser);

  // 4️⃣ Crear carrito vacío y asociarlo al usuario
  Cart cart = new Cart();
  cart.setCustomer(savedUser);
  cartRepository.save(cart);

  // 5️⃣ Autenticar al usuario recién creado usando AuthenticationManager
  Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  user.getEmail(),        // username
                  user.getPassword()      // password en texto plano (será comparado con el hash en DB)
          )
  );

  // 6️⃣ Guardar la autenticación en el contexto de Spring Security
  SecurityContextHolder.getContext().setAuthentication(authentication);

  // 7️⃣ Generar el token JWT para el usuario autenticado
  String jwt = jwtProvider.generateToken(authentication);

  // 8️⃣ Preparar y devolver la respuesta
  AuthResponse authResponse = new AuthResponse();
  authResponse.setJwt(jwt);
  authResponse.setMessage("¡Usuario creado exitosamente!");
  authResponse.setRole(savedUser.getRole());

  return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
 }

}
