package com.diver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una categoría de ingredientes en el sistema de pedidos de comida online.
 *
 * Relaciones principales:
 * - Many-to-One con Restaurant: Cada categoría pertenece a un restaurante específico
 * - One-to-Many con IngredientItem: Una categoría puede contener múltiples ingredientes
 *
 * @example Categoría "Lácteos"
 *   - Restaurante: "La Trattoria" (Restaurant)
 *   - Ingredientes: "Queso Mozzarella", "Queso Parmesano", "Nata" (IngredientItem)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ingredient_category")
public class IngredientCategory {
    /**
     * Identificador único de la categoría
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Nombre de la categoría de ingredientes
     *
     * @example "Lácteos", "Verduras", "Carnes", "Especias"
     */
    private String name;

    /**
     * Restaurante al que pertenece esta categoría
     * La anotación @JsonIgnore evita la recursión infinita al serializar a JSON
     *
     * @example Restaurant(id=1, name="La Trattoria")
     */
    @JsonIgnore
    @ManyToOne
    private Restaurant restaurant;

    /**
     * Lista de ingredientes que pertenecen a esta categoría
     * - mappedBy = "category" indica que la relación se encuentra en la clase IngredientItem
     * - cascade = CascadeType.ALL indica que se deben eliminar todos los ingredientes cuando se elimine la categoría
     * - orphanRemoval = true indica que se deben eliminar los ingredientes huérfanos
     *
     * @example [IngredientItem(id=1, name="Queso Mozzarella"), IngredientItem(id=2, name="Queso Parmesano")]
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientItem> ingredients = new ArrayList<>();
}