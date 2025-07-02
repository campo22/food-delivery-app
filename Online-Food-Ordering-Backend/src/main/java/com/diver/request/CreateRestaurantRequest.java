package com.diver.request;

import com.diver.model.Address;
import com.diver.model.ContactInformation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.List;

/**
 * Solicitud para crear un restaurante.
 * Contiene la información necesaria para registrar un nuevo restaurante.
 */
@Data
public class CreateRestaurantRequest {




        /**
         * (Opcional) ID del restaurante (solo para actualizar).
         */
        private Long id;

        /**
         * Nombre del restaurante (requerido).
         */
        @NotBlank(message = "El nombre del restaurante es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        private String name;

        /**
         * Descripción del restaurante.
         */
        @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
        private String description;

        /**
         * Tipo de cocina (requerido).
         */
        @NotBlank(message = "El tipo de cocina es obligatorio")
        private String cuisineType;

        /**
         * Horario de apertura (opcional pero validable si se usa).
         */
        @Size(max = 100, message = "El horario de apertura no puede tener más de 100 caracteres")
        private String openingHours;

        /**
         * Dirección del restaurante (requerido).
         */
        @NotNull(message = "La dirección es obligatoria")
        private Address address;

        /**
         * Información de contacto (opcional).
         */
        private ContactInformation contactInformation;

        /**
         * Lista de URLs o nombres de imágenes (opcional).
         */
        private List<@NotBlank(message = "La imagen no puede estar vacía") String> images;
    }


