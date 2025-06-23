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
import io.swagger.v3.oas.annotations.Operation;
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

/**
 * Controlador REST para gestionar la autenticación y autorización de usuarios.
 *
 * Este controlador maneja:
 * - Registro de nuevos usuarios (signup)
 * - Inicio de sesión de usuarios existentes (signin)
 * - Generación y validación de tokens JWT
 * - Creación automática de carritos de compra para nuevos usuarios
 */
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

 /**
  * Registra un nuevo usuario en el sistema.
  *
  * Proceso de registro:
  * 1. Verifica que el email no esté ya registrado
  * 2. Crea el usuario con la información proporcionada
  * 3. Encripta la contraseña antes de guardarla
  * 4. Asigna el rol especificado en la petición
  * 5. Crea un carrito de compras vacío asociado al usuario
  * 6. Autentica automáticamente al usuario recién creado
  * 7. Genera y retorna un token JWT
  *
  * @param user Datos del usuario a registrar (email, nombre, contraseña, rol)
  * @return ResponseEntity con el token JWT y datos de autenticación
  * @throws Exception si el email ya existe en el sistema o hay errores de autenticación
  */
 @Operation(
         summary = "Crear un nuevo usuario",
         description = "Se puede registrar un nuevo usuario al sistema."
 )
 @PostMapping("/signup")
 public ResponseEntity<AuthResponse> createUserHandle(@RequestBody User user) throws Exception {
  try {
   // 1️⃣ Verificar si el correo ya está registrado
   User existingUser = userRepository.findByEmail(user.getEmail());
   if (existingUser != null) {
    throw new Exception("Este email ya existe en el sistema");
   }

   // 2️⃣ Crear nuevo usuario con los datos recibidos
   User newUser = new User();
   newUser.setEmail(user.getEmail());
   newUser.setFullName(user.getFullName());
   newUser.setPassword(passwordEncoder.encode(user.getPassword()));

   // 3️⃣ Asignar rol (o usar ROLE_CUSTOMER por defecto)
   USER_ROLE roleToAssign = user.getRole() != null ? user.getRole() : USER_ROLE.ROLE_CUSTOMER;
   newUser.setRole(roleToAssign);

   // 4️⃣ Guardar el usuario en la base de datos
   User savedUser = userRepository.save(newUser);

   // 5️⃣ Crear carrito de compras vacío asociado al usuario
   Cart cart = new Cart();
   cart.setCustomer(savedUser);
   cartRepository.save(cart);

   // 6️⃣ Autenticar al usuario recién registrado
   Authentication authentication = authenticationManager.authenticate(
           new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
   );

   // 7️⃣ Registrar la autenticación en el contexto de seguridad
   SecurityContextHolder.getContext().setAuthentication(authentication);

   // 8️⃣ Generar el token JWT
   String jwt = jwtProvider.generateToken(authentication);

   // 9️⃣ Preparar y devolver la respuesta
   AuthResponse authResponse = new AuthResponse();
   authResponse.setJwt(jwt);
   authResponse.setMessage("¡Usuario creado exitosamente!");
   authResponse.setRole(savedUser.getRole());

   return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

  } catch (Exception e) {
   System.err.println("Error en el proceso de registro: " + e.getMessage());
   throw new Exception("Error al registrar usuario: " + e.getMessage());
  }
 }


 /**
  * Autentica un usuario existente en el sistema.
  *
  * Proceso de autenticación:
  * 1. Valida las credenciales del usuario con Spring Security
  * 2. Extrae el rol del usuario autenticado
  * 3. Genera un token JWT válido
  * 4. Retorna la respuesta con el token y información del usuario
  *
  * @param req Credenciales de inicio de sesión (email y contraseña)
  * @return ResponseEntity con el token JWT y datos de autenticación
  * @throws BadCredentialsException si las credenciales son incorrectas
  */
 @Operation(
         summary = "Loguearse usuario existente",
         description = "Los usuario ya registrados pueden loguearse en el sistema"
 )
 @PostMapping("/signin")
 public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
  try {
   // 1️⃣ Autenticar al usuario usando Spring Security
   Authentication authentication = authenticationManager.authenticate(
           new UsernamePasswordAuthenticationToken(
                   req.getEmail(), req.getPassword()
           )
   );

   // 2️⃣ Obtener el primer rol del usuario (GrantedAuthority)
   String fullRole = authentication.getAuthorities()
           .stream()
           .findFirst()
           .map(GrantedAuthority::getAuthority) // Ej: "ROLE_RESTAURANT_OWNER"
           .orElse("ROLE_CUSTOMER");            // Valor por defecto si no hay roles

   // 3️⃣ Convertir a enum USER_ROLE (asegurándote que coincida exactamente con los valores del enum)
   USER_ROLE userRole;
   try {
    userRole = USER_ROLE.valueOf(fullRole); // Usa directamente el rol con prefijo
   } catch (IllegalArgumentException e) {
    System.err.println("Rol no reconocido: " + fullRole + ", se usará ROLE_CUSTOMER por defecto");
    userRole = USER_ROLE.ROLE_CUSTOMER;
   }

   // 4️⃣ Generar token JWT para el usuario autenticado
   String jwt = jwtProvider.generateToken(authentication);

   // 5️⃣ Preparar y retornar la respuesta
   AuthResponse authResponse = new AuthResponse();
   authResponse.setJwt(jwt);
   authResponse.setMessage("Autenticación exitosa");
   authResponse.setRole(userRole);

   return new ResponseEntity<>(authResponse, HttpStatus.OK);

  } catch (BadCredentialsException e) {
   System.err.println("Intento fallido de login para email: " + req.getEmail());
   throw new BadCredentialsException("Email o contraseña incorrectos");
  } catch (Exception e) {
   System.err.println("Error inesperado durante el login: " + e.getMessage());
   throw new RuntimeException("Error interno durante la autenticación", e);
  }
 }

}