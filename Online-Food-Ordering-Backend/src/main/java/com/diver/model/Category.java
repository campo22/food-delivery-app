package com.diver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Clase entidad que representa una Categoría en el sistema de restaurante.
 * Esta clase gestiona las categorías de productos/artículos dentro de un restaurante.
 *
 * Relaciones:
 * - Relación Muchos-a-Uno con Restaurante: Cada categoría pertenece a un restaurante
 * - Relación Uno-a-Muchos (implícita): Una categoría puede contener múltiples productos/artículos
 *
 * La entidad Categoría es esencial para organizar y agrupar elementos del menú
 * en una estructura jerárquica dentro del sistema de restaurante.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {
    /**
     * Identificador único para la categoría.
     * Auto-generado utilizando una secuencia de base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Nombre de la categoría.
     * Utilizado para identificar y mostrar la categoría en el menú.
     */
    private String name;

    /**
     * Restaurante al que pertenece esta categoría.
     * Establece una relación Muchos-a-Uno con la entidad Restaurante.
     * JsonIgnore previene la recursión infinita en la serialización JSON.
     */
    @JsonIgnore
    @ManyToOne
    private Restaurant restaurant;
}