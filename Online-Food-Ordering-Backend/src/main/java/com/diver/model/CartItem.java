package com.diver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Cart  cart; // hace referencia al carrito


    @ManyToOne
    private Food food; // hace referencia a la comida

    private int quantity; // cantidad de comida

    @ElementCollection
    private List<String> ingredients; // lista de ingredientes

    private Long totalPrice; // precio total de la comida

}