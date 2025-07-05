package com.diver.controller;

import com.diver.dto.UserProfileDto;
import com.diver.model.User;
import com.diver.service.UserService; // Importa la interfaz, no la implementación
// ... otras importaciones ...
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Inyecta la interfaz

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal User user) {
        // Llama al nuevo método del servicio para obtener el DTO.
        UserProfileDto userProfile = userService.getUserProfileByEmail(user.getEmail());
        return ResponseEntity.ok(userProfile);
    }
}