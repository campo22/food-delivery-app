
package com.diver.model;

import com.diver.dto.RestaurantDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
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
public class User implements UserDetails {

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

    // =================================================================================
    // --- IMPLEMENTACIÓN DE LOS MÉTODOS DE LA INTERFAZ UserDetails ---
    // =================================================================================

    /**
     * Devuelve los permisos concedidos al usuario. Spring Security utiliza esta información
     * para las comprobaciones de autorización (p. ej., @PreAuthorize("hasRole('ADMIN')")).
     *
     * @return una colección de {@link GrantedAuthority} que representan los roles del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertimos nuestro enum USER_ROLE en un objeto que Spring Security entiende.
        // El método .name() del enum nos da el String, ej: "ROLE_ADMIN".
        if (role == null) {
            return List.of(); // Devolver una lista vacía si no hay rol para evitar NullPointerException.
        }
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Devuelve la contraseña utilizada para autenticar al usuario.
     *
     * @return el hash de la contraseña del usuario.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Devuelve el nombre de usuario utilizado para autenticar al usuario. En nuestro caso,
     * utilizamos el correo electrónico como identificador único.
     *
     * @return el email del usuario.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Indica si la cuenta del usuario ha expirado. Una cuenta expirada no puede ser autenticada.
     * Para la mayoría de los casos, devolver 'true' es suficiente.
     *
     * @return {@code true} si la cuenta es válida (no ha expirado).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está bloqueado o desbloqueado. Un usuario bloqueado no puede ser autenticado.
     * Para la mayoría de los casos, devolver 'true' es suficiente.
     *
     * @return {@code true} si la cuenta no está bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario (contraseña) han expirado.
     * Para la mayoría de los casos, devolver 'true' es suficiente.
     *
     * @return {@code true} si las credenciales no han expirado.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado o deshabilitado. Un usuario deshabilitado no puede ser autenticado.
     *
     * @return {@code true} si el usuario está habilitado.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}


