package com.diver.request;

import com.diver.model.Address;
import com.diver.model.ContactInformation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Solicitud para crear un restaurante.
 * Contiene la información necesaria para registrar un nuevo restaurante.
 */
@Data
public class CreateRestaurantRequest {

    /**
     * ID del restaurante.
     */
    private Long id;

    /**
     * Nombre del restaurante.
     */
    private String name;

    /**
     * Descripción del restaurante.
     */
    private String description;

    /**
     * Tipo de cocina del restaurante.
     */
    private String cuisineType;

    /**
     * Horario de apertura del restaurante.
     */
    private String openingHours;

    /**
     * Dirección del restaurante.
     */
    private Address address;

    /**
     * Información de contacto del restaurante.
     */
    private ContactInformation contactInformation;

    /**
     * Imágenes del restaurante.
     */
    private List<String> images;

}
