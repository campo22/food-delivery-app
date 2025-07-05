package com.diver.controller;

import com.diver.dto.AuthenticatedUser;
import com.diver.dto.RestaurantDto;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.request.CreateRestaurantRequest;
import com.diver.service.RestaurantService;
import com.diver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión administrativa de restaurantes.
 * <p>
 * Este controlador expone endpoints para operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * sobre la entidad Restaurante. Actúa como la capa de entrada HTTP, delegando toda la
 * lógica de negocio y de seguridad a la capa de servicio ({@link RestaurantService}).
 * <p>
 * Requiere que el usuario esté autenticado y posea el rol 'ADMIN' o 'RESTAURANT_OWNER'
 * para acceder a sus funcionalidades.
 *
 * @author Tu Nombre (o el nombre del equipo)
 * @version 1.2
 * @since 2023-10-27
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/restaurant")
@RequiredArgsConstructor
@Tag(
        name = "Admin: Restaurant Management",
        description = "Endpoints para la gestión de restaurantes por administradores y propietarios.")
@SecurityRequirement(name = "bearerAuth") // Asume que tienes configurado un securityScheme 'bearerAuth' en tu config de OpenAPI
public class AdminRestaurantController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    /**
     * Crea un nuevo restaurante en el sistema.
     * <p>
     * El usuario autenticado se asignará como el propietario del restaurante.
     * Valida la regla de negocio que impide a un 'RESTAURANT_OWNER' crear más de un restaurante.
     *
     * @param req El DTO con los datos para la creación del restaurante.
     * @param user El usuario autenticado, inyectado por Spring Security.
     * @return Un {@link ResponseEntity} con el restaurante creado y un estado HTTP 201 (Created).
     */
    @Operation(
            summary = "Crear un nuevo restaurante",
            description = "Crea un restaurante y lo asocia al usuario autenticado como propietario. " +
                          "Un 'RESTAURANT_OWNER' solo puede crear uno."
    )
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Restaurante creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class
                    ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado (rol no permitido)",
                    content = @Content),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto de regla de negocio (p. ej., " +
                    "propietario ya tiene restaurante)", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantDto> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest req,
            @AuthenticationPrincipal User user
            ) {
        log.info("Usuario '{}' solicita la creación de un restaurante con nombre '{}'.",
                user.getEmail(), req.getName());
        User owner = userService.findUserById( user.getId() );
        RestaurantDto restaurant = restaurantService.createRestaurant(req, owner);
        return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }

    /**
     * Actualiza un restaurante existente.
     * <p>
     * El usuario debe ser 'ADMIN' o el propietario del restaurante para poder realizar esta acción.
     *
     * @param id El ID del restaurante a actualizar.
     * @param req El DTO con los nuevos datos del restaurante.
     * @param user El usuario autenticado, para validación de permisos.
     * @return Un {@link ResponseEntity} con el restaurante actualizado y un estado HTTP 200 (OK).
     */
    @Operation(
            summary = "Actualizar un restaurante existente",
            description = "Actualiza los detalles de un restaurante por su ID. Requiere ser ADMIN o el propietario.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurante actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado (no es propietario o ADMIN)",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> updateRestaurant(
            @Parameter(description = "ID del restaurante a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CreateRestaurantRequest req,
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' solicita la actualización del restaurante con ID {}.", user.getEmail(), id);
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, req, user);
        return ResponseEntity.ok(updatedRestaurant);
    }

    /**
     * Elimina un restaurante del sistema.
     * <p>
     * Esta es una operación destructiva. El usuario debe ser 'ADMIN' o el propietario del restaurante.
     *
     * @param id El ID del restaurante a eliminar.
     * @param user El usuario autenticado, para validación de permisos.
     * @return Un {@link ResponseEntity} sin contenido y un estado HTTP 204 (No Content).
     */
    @Operation(summary = "Eliminar un restaurante",
            description = "Elimina permanentemente un restaurante por su ID. Requiere ser ADMIN o el propietario.")
    @ApiResponses(value = {
            @ApiResponse
                    (responseCode = "204",
                            description = "Restaurante eliminado exitosamente",
                            content = @Content),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado (no es propietario o ADMIN)",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteRestaurant(
            @Parameter(description = "ID del restaurante a eliminar", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        log.warn("Usuario '{}' solicita la eliminación del restaurante con ID {}. ¡Acción crítica!", user.getEmail(), id);
        restaurantService.deleteRestaurant(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia el estado de apertura de un restaurante (abierto/cerrado).
     *
     * @param id El ID del restaurante cuyo estado se cambiará.
     * @param user El usuario autenticado, para validación de permisos.
     * @return Un {@link ResponseEntity} con el restaurante y su estado actualizado, y un estado HTTP 200 (OK).
     */
    @Operation(summary = "Alternar el estado de apertura de un restaurante",
            description = "Abre o cierra un restaurante. Requiere ser ADMIN o el propietario.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado del restaurante actualizado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado (no es propietario o ADMIN)",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content)
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> updateRestaurantStatus(
            @Parameter(description = "ID del restaurante para cambiar su estado", required = true, example = "1")

            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' solicita cambiar el estado del restaurante con ID {}.", user.getEmail(), id);
        Restaurant restaurant = restaurantService.updateRestaurantStatus(id, user);
        return ResponseEntity.ok(restaurant);
    }

    /**
     * Obtiene el restaurante propiedad del usuario autenticado.
     * <p>
     * Este endpoint es un atajo para que un 'RESTAURANT_OWNER' obtenga los datos de su propio restaurante
     * sin necesidad de conocer su ID.
     *
     * @param user El usuario autenticado.
     * @return Un {@link ResponseEntity} con el restaurante encontrado y un estado HTTP 200 (OK).
     */
    @Operation(summary = "Obtener mi restaurante",
            description = "Devuelve el restaurante propiedad del usuario autenticado. " +
                    "Principalmente para 'RESTAURANT_OWNER'."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurante encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class))),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado (rol no permitido)",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "El usuario no posee un restaurante",
                    content = @Content)
    })
    @GetMapping("/my-restaurant")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> getRestaurantByUserId(
            @AuthenticationPrincipal User user
    ) {
        log.debug("Usuario '{}' solicita los datos de su propio restaurante.", user.getEmail());
        Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());
        return ResponseEntity.ok(restaurant);
    }
}