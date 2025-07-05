package com.diver.dto;

import com.diver.model.Address;

import java.util.List;

// Usar records de Java para DTOs inmutables es una práctica moderna y concisa.
public record UserProfileDto(
        Long id,
        String fullName,
        String email,
        String role,
        List<AdddToFavoritesDto> favorites,
        List<Address> addresses // Asumiendo que Address no tiene más relaciones lazy
) {}