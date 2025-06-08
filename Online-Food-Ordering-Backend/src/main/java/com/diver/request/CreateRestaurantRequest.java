package com.diver.request;

import com.diver.model.Address;
import com.diver.model.ContactInformation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateRestaurantRequest {

    private Long id ; // ID del restaurante
    private String name ; // Nombre del restaurante
    private String description ; // Descripción del restaurante
    private String cuisineType ; // Tipo de cocina del restaurante
    private String openingHours ; // Horario de apertura del restaurante
    private Address address ; // Direccion del restaurante
    private ContactInformation contactInformation ; // Información de contacto del restaurante
    private List<String> images ; // Imagenes del restaurante


}
