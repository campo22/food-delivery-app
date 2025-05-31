package com.diver.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa el carrito de compras.
 * Relaciones:
 * - OneToOne con User: Un carrito pertenece a un único cliente
 * - OneToMany con CartItem: Un carrito puede tener múltiples items
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {
    /**
     * Identificador único del carrito
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Cliente al que pertenece el carrito
     */
    @OneToOne
    private User customer;

    /**
     * Lista de items en el carrito
     * mappedBy indica que la relación es bidireccional y está mapeada por el campo cart en CartItem
     * cascade indica que las operaciones se propagan a los items
     * orphanRemoval permite eliminar items cuando se eliminan del carrito
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Monto total del carrito
     */
    private Long total;
}