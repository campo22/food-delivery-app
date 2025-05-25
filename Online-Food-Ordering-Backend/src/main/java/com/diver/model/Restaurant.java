package com.diver.model;

// Importaciones necesarias
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Restaurant que representa un restaurante en el sistema de pedidos de comida online.
 *
 * Relaciones principales:
 * @One-to-One con User: Cada restaurante tiene un único propietario
 * @One-to-One con Address: Cada restaurante tiene una única dirección física
 * @One-to-Many con Order: Un restaurante puede tener múltiples pedidos
 * @One-to-Many con Food: Un restaurante ofrece múltiples platos en su menú
 *
 * @example Restaurante "La Trattoria"
 *   - Propietario: Mario Rossi (User)
 *   - Dirección: Calle Principal 123, Madrid (Address)
 *   - Platos: Pizza Margherita, Pasta Carbonara, Tiramisú (Food)
 *   - Órdenes: Pedido #1001, Pedido #1002, Pedido #1003 (Order)
 *
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurante")
public class Restaurant {

    /**
     * Identificador único del restaurante (Primary Key)
     * //
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Propietario del restaurante
     * Relación uno a uno con la entidad User
     *
     * @example User(id=1, fullName="Mario Rossi", role=OWNER)
     */
    @OneToOne
    private User owner;

    /**
     * Nombre del restaurante
     *
     * @example "La Trattoria", "Sushi Express", "Taco Loco"
     */
    private String name;

    /**
     * Descripción del restaurante
     *
     * @example "Auténtica cocina italiana con recetas tradicionales de la Toscana"
     */
    private String description;

    /**
     * Tipo de cocina del restaurante
     *
     * @example "Italiana", "Japonesa", "Mexicana", "Mediterránea"
     */
    private String cuisineType;

    /**
     * Dirección física del restaurante
     * Relación uno a uno con la entidad Address
     *
     * @example Address(id=1, street="Calle Principal 123", city="Madrid", postalCode="28001")
     */
    @OneToOne
    private Address address;

    /**
     * Información de contacto del restaurante
     * Embebida directamente en la tabla del restaurante
     * Incluye teléfono, correo electrónico, sitio web, etc.
     *
     * @example ContactInformation(phone="+34612345678", email="info@latrattoria.com")
     */
    @Embedded
    private ContactInformation contactInformation;

    /**
     * Horario de apertura del restaurante
     *
     * @example "Lunes a Viernes: 12:00 - 23:00, Sábados y Domingos: 13:00 - 00:00"
     */
    private String openingHours;

    /**
     * Lista de órdenes del restaurante
     * Relación uno a muchos con la entidad Order
     * - mappedBy: La relación es controlada por el campo "restaurant" en Order
     * - cascade: Las operaciones en el restaurante afectan a sus órdenes
     * - orphanRemoval: Si se elimina una orden de la lista, se elimina de la BD
     *
     * @example [Order(id=1001, totalAmount=2500), Order(id=1002, totalAmount=1800)]
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    /**
     * Galería de imágenes del restaurante
     * Almacenadas como URLs en una tabla separada
     * El atributo length permite almacenar URLs largas
     *
     * @example ["https://example.com/images/restaurant1.jpg", "https://example.com/images/interior.jpg"]
     */
    @ElementCollection
    @Column(length = 1000)
    private List<String> images;

    /**
     * Fecha de registro del restaurante en el sistema
     *
     * @example "2023-01-15T10:30:00"
     */
    private LocalDateTime registrationDate;

    /**
     * Indica si el restaurante está actualmente abierto o cerrado
     *
     * @example true (abierto), false (cerrado)
     */
    private boolean open;

    /**
     * Lista de platos ofrecidos por el restaurante
     * Relación uno a muchos con la entidad Food
     *
     * La anotación @JsonIgnore evita que esta propiedad se incluya en la serialización JSON.
     * Esto previene problemas de recursión infinita cuando se serializa un restaurante.
     *
     * @example
     * Sin @JsonIgnore, al serializar un restaurante a JSON obtendríamos:
     * {
     *   "id": 1,
     *   "name": "La Trattoria",
     *   "foods": [
     *     {
     *       "id": 101,
     *       "name": "Pizza Margherita",
     *       "restaurant": {
     *         "id": 1,
     *         "name": "La Trattoria",
     *         "foods": [
     *           { ... } // Recursión infinita aquí
     *         ]
     *       }
     *     }
     *   ]
     * }
     *
     * Con @JsonIgnore, el JSON resultante sería:
     * {
     *   "id": 1,
     *   "name": "La Trattoria"
     *   // El campo "foods" no aparece en el JSON
     * }
     */
    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Food> foods = new ArrayList<>();
}