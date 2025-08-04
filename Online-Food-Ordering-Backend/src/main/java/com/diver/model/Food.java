
package com.diver.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase que modela un plato o artículo de comida en el sistema.
 * Almacena toda la información relevante para la gestión de productos alimenticios
 * en un restaurante, incluyendo sus características, precio, categoría y disponibilidad.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "food")
public class    Food {
    /**
     * ID único de la comida (clave primaria en la base de datos)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Nombre del plato (ej: "Paella", "Sushi")
     */
    private String name;

    /**
     * Descripción detallada del plato
     */
    private String description; // descripción de la comida

    /**
     * Precio en unidades monetarias (en céntimos para evitar decimales)
     */
    private Long price; // precio de la comida

    /**
     * Relación con la categoría a la que pertenece este plato
     *
     * @see Category
     */
    @ManyToOne
    private Category category; // categoría de la comida

    /**
     * Lista de URLs o rutas de imágenes del plato
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images;

    /**
     * Indica si el plato está actualmente disponible para venta
     */
    private boolean available;

    /**
     * Restaurante al que pertenece este plato
     * Relación muchos a uno: muchos platos pueden pertenecer a un restaurante
     * y un restaurante puede tener muchos platos
     * @see Restaurant
     */
    @ManyToOne
    private Restaurant restaurant;

    /**
     * Indica si el plato es vegetariano
     */
    private boolean isVegetarian;

    /**
     * Indica si el plato es de temporada
     */
    private boolean isSeasonal;

    /**
     * Lista de ingredientes utilizados en este plato
     * @see IngredientItem
     */
    @ManyToMany
    private List<IngredientItem> ingredients= new ArrayList<>();

    /**
     * Fecha de creación del registro de este plato
     */
    private Date creationDate;
}