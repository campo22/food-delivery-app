package com.diver.controller;

import com.diver.model.User;
import com.diver.service.Imp.UserServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImp userServiceImp;

    @Operation(
            summary = "Obtener informacion personal de un usuario",
            description = "Se obtiene la informacion personal de un usuario registrado y logueado."
    )
    @GetMapping("/profile")
    public ResponseEntity<User> findUserByJwtToken(@RequestHeader("Authorization") String jwt) throws Exception {
        return ResponseEntity.ok(userServiceImp.findUserByJwt(jwt));
    }
}
