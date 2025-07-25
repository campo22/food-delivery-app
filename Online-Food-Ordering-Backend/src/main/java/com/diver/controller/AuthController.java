package com.diver.controller;

import com.diver.model.User;
import com.diver.request.LoginRequest;
import com.diver.response.AuthResponse;
import com.diver.service.Imp.AuthServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gestionar la autenticación y el registro de usuarios.
 * <p>
 * Este controlador expone endpoints públicos para que los usuarios puedan
 * crear una cuenta (signup) e iniciar sesión (signin). Delega toda la lógica
 * de negocio al {@link AuthServiceImp}.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints para registro e inicio de sesión.")
public class AuthController {

 private final AuthServiceImp authService;

 /**
  * Maneja la solicitud de registro de un nuevo usuario.
  *
  * @param userRequest Un objeto User que contiene los datos de registro.
  *                    Se utiliza como DTO para la petición.
  * @return un ResponseEntity con la respuesta de autenticación y un estado 201 Created.
  */
 @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema.")
 @PostMapping("/signup")
 public ResponseEntity<AuthResponse> createUserHandle(@RequestBody User userRequest) {
  AuthResponse response = authService.registerUser(userRequest);
  return new ResponseEntity<>(response, HttpStatus.CREATED);
 }

 /**
  * Maneja la solicitud de inicio de sesión de un usuario existente.
  *
  * @param req El DTO con las credenciales de inicio de sesión (email y contraseña).
  * @return un ResponseEntity con la respuesta de autenticación y un estado 200 OK.
  */
 @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y le devuelve un token JWT.")
 @PostMapping("/signin")
 public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
  AuthResponse response = authService.loginUser(req);
  return new ResponseEntity<>(response, HttpStatus.OK);
 }

}