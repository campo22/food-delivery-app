
package com.diver.model;

import com.diver.dto.RestaurantDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un usuario dentro del sistema de gestión de pedidos del restaurante.
 * <p>
 * Un usuario puede ser cliente, propietario de restaurante o administrador, y contiene información personal,
 * credenciales, roles y relaciones con órdenes, direcciones y restaurantes favoritos.
 * </p>
 *
 * <p>
 * Relaciones principales:
 * <ul>
 *   <li>Un usuario puede tener muchas órdenes.</li>
 *   <li>Un usuario puede tener muchas direcciones.</li>
 *   <li>Un usuario puede tener una colección de restaurantes favoritos.</li>
 * </ul>
 * </p>
 *
 * @author Equipo de Desarrollo
 * @version 1.0
 * @since 2024-06
 */
@Entity
@Data // Lombok: genera getters, setters, toString, equals, hashCode y constructor requerido.
@AllArgsConstructor // Lombok: genera constructor con todos los atributos.
@NoArgsConstructor  // Lombok: genera constructor sin parámetros.
public class User {

    /**
     * Identificador único del usuario.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    /**
     * Nombre completo del usuario.
     */
    private String fullName;

    /**
     * Dirección de correo electrónico del usuario.
     */
    private String email;

    /**
     * Contraseña de la cuenta del usuario.
     * Este campo es protegido con @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
     * para evitar que se muestre en la serialización JSON.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * Rol del usuario, definido por el enum USER_ROLE.
     * Puede ser cliente, propietario de restaurante o administrador.
     */
    private USER_ROLE role=USER_ROLE.ROLE_CUSTOMER; // Por defecto, un usuario es cliente;

    /**
     * Lista de órdenes asociadas al usuario.
     * Relación uno a muchos: un usuario puede tener muchas órdenes.
     * Se ignora en la serialización JSON para evitar ciclos infinitos.
     * Al eliminar un usuario, se eliminan sus órdenes asociadas.
     * El mappedBy indica que la relación es bidireccional y se mapea por el atributo "customer" en la clase Order.
     */
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    /**
     * Colección de restaurantes favoritos del usuario, representados por objetos RestaurantDto.
     */
    @ElementCollection
    private List<RestaurantDto> favorites = new ArrayList<>();

    /**
     * Lista de direcciones asociadas al usuario.
     * Relación uno a muchos: un usuario puede tener muchas direcciones.
     * Al eliminar un usuario, se eliminan sus direcciones asociadas.
     * Si se elimina una dirección de la lista, también se elimina de la base de datos.
     * El orphanRemoval = true asegura que las direcciones huérfanas se eliminen automáticamente.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

}
