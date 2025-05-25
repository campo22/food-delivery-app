package com.diver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa un ingrediente en el sistema.
 * Relaciones:
 * - ManyToOne con IngredientCategory: Un ingrediente pertenece a una categoría
 * - ManyToOne con Restaurant: Un ingrediente pertenece a un restaurante
 * - Un restaurante puede tener múltiples ingredientes
 * - Una categoría puede tener múltiples ingredientes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class IngredientItem {
    /**
     * Identificador único del ingrediente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Nombre del ingrediente
     */
    private String name;

    /**
     * Categoría a la que pertenece el ingrediente
     * Relación muchos a uno con IngredientCategory
     */
    @ManyToOne
    private IngredientCategory category;

    /**
     * Restaurante al que pertenece el ingrediente
     * Relación muchos a uno con Restaurant
     * Se ignora en la serialización JSON
     */
    @JsonIgnore
    @ManyToOne
    private Restaurant restaurant;

    /**
     * Indica si el ingrediente está disponible en el inventario del restaurante
     */
    private boolean inStock;
}