package com.diver.dto;

import com.diver.model.IngredientItem; // Asumiendo que IngredientItem es seguro para serializar
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * DTO (Data Transfer Object) para representar la información de un plato de comida (Food)
 * de forma segura y controlada en las respuestas de la API.
 * <p>
 * Este objeto desacopla la representación de la API de la entidad de persistencia (JPA),
 * seleccionando cuidadosamente qué campos exponer y aplanando las relaciones complejas
 * para evitar problemas de serialización y sobreexposición de datos.
 */
@Data
public class    FoodDto {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private List<String> images;
    private boolean available;
    private boolean vegetarian; // Renombrado de isVegetarian para seguir convenciones de DTO
    private boolean seasonal;   // Renombrado de isSeasonal

    // Aplanamos las relaciones complejas para la respuesta

    /**
     * Información simplificada de la categoría a la que pertenece el plato.
     */
    private CategoryDto category;

    /**
     * Información simplificada del restaurante que ofrece el plato.
     */
    private RestaurantSimpleDto restaurant;

    /**
     * Lista de ingredientes del plato. Se asume que IngredientItem es un DTO o
     * una entidad simple y segura para serializar.
     */
    private List<IngredientItemDto> ingredients;

    private Date creationDate;


    // --- Sub-DTOs internos para aplanar las relaciones ---

    /**
     * DTO simple para representar la información esencial de una categoría.
     */
    @Data
    public static class CategoryDto {
        private Long id;
        private String name;
    }

    /**
     * DTO simple para representar la información esencial de un restaurante.
     */
    @Data
    public static class RestaurantSimpleDto {
        private Long id;
        private String name;
    }

}