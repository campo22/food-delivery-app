package com.diver.controller;

import com.diver.exception.RestaurantNotFoundException;
import com.diver.exception.UnauthorizedException;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.request.CreateRestaurantRequest;
import com.diver.response.MessageResponse;
import com.diver.service.RestaurantService;
import com.diver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar restaurantes desde el rol ADMIN o RESTAURANT_OWNER.
 * Implementa validación granular de permisos y auditoría de acciones.
 *
 * @author Tu Nombre
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/restaurant")
@RequiredArgsConstructor // Agregar esta anotación para inyectar dependencias con @Autowired en el constructor
@Validated // Agregar esta anotación para validar los datos de entrada
@Tag(name = "Admin Restaurant Controller", description = "Gestión administrativa de restaurantes")
public class AdminRestaurantController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    /**
     * 🏗️ Crear un restaurante.
     */
    @Operation(summary = "Crear restaurante", description = "Crea un restaurante nuevo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @PostMapping
    @CacheEvict(value = "userRestaurant", allEntries = true)
    public ResponseEntity<Restaurant> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt
    ) throws RestaurantNotFoundException, UnauthorizedException {
        try {
            User user = userService.findUserByJwt(jwt);

            log.info("Usuario {} ({}) creando restaurante: {}",
                    user.getEmail(), user.getRole(), req.getName());

            // Validar que RESTAURANT_OWNER no tenga ya un restaurante
            if ("RESTAURANT_OWNER".equals(user.getRole())) {
                validateSingleRestaurantOwnership(user);
            }

            Restaurant restaurant = restaurantService.createRestaurant(req, user);

            log.info("Restaurante creado exitosamente con ID: {}", restaurant.getId());
            return new ResponseEntity<>(restaurant, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error al crear restaurante: {}", e.getMessage());
            throw new UnauthorizedException("Token JWT inválido o usuario no encontrado");
        }
    }

    /**
     * ✏️ Actualizar restaurante existente.
     */
    @Operation(summary = "Actualizar restaurante", description = "Actualiza los datos de un restaurante existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para modificar este restaurante"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @PutMapping("/{id}")
    @CacheEvict(value = "userRestaurant", allEntries = true)
    public ResponseEntity<Restaurant> updateRestaurant(
            @Valid @RequestBody CreateRestaurantRequest req,
            @PathVariable @Min(1) Long id,
            @RequestHeader("Authorization") String jwt
    ) throws RestaurantNotFoundException, UnauthorizedException {
        try {
            User user = userService.findUserByJwt(jwt);

            log.info("Usuario {} ({}) actualizando restaurante ID: {}",
                    user.getEmail(), user.getRole(), id);

            // Validación granular de permisos
            if ("RESTAURANT_OWNER".equals(user.getRole())) {
                validateRestaurantOwnership(user, id);
            }

            Restaurant restaurant = restaurantService.updateRestaurant(id, req);

            log.info("Restaurante ID: {} actualizado exitosamente", id);
            return ResponseEntity.ok(restaurant);

        } catch (RestaurantNotFoundException | UnauthorizedException e) {
            throw e; // Re-lanzar excepciones específicas
        } catch (Exception e) {
            log.error("Error al actualizar restaurante: {}", e.getMessage());
            throw new UnauthorizedException("Token JWT inválido o usuario no encontrado");
        }
    }

    /**
     * 🗑️ Eliminar un restaurante.
     */
    @Operation(summary = "Eliminar restaurante", description = "Elimina un restaurante por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para eliminar este restaurante"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @DeleteMapping("/{id}")
    @CacheEvict(value = "userRestaurant", allEntries = true)
    public ResponseEntity<MessageResponse> deleteRestaurant(
            @PathVariable @Min(1) Long id,
            @RequestHeader("Authorization") String jwt
    ) throws RestaurantNotFoundException, UnauthorizedException {
        try {
            User user = userService.findUserByJwt(jwt);

            log.warn("Usuario {} ({}) eliminando restaurante ID: {}",
                    user.getEmail(), user.getRole(), id);

            // Validación granular de permisos ejemp:
            // 1. ADMIN puede eliminar cualquier restaurante
            // 2. RESTAURANT_OWNER solo puede eliminar su propio restaurante
            if ("RESTAURANT_OWNER".equals(user.getRole())) {
                validateRestaurantOwnership(user, id);
            }

            restaurantService.deleteRestaurant(id);

            MessageResponse response = MessageResponse.builder()
                    .message("Restaurante eliminado correctamente")
                    .success(true)
                    .build();

            log.info("Restaurante ID: {} eliminado exitosamente", id);
            return ResponseEntity.ok(response);

        } catch (RestaurantNotFoundException | UnauthorizedException e) {
            throw e; // Re-lanzar excepciones específicas
        } catch (Exception e) {
            log.error("Error al eliminar restaurante: {}", e.getMessage());
            throw new UnauthorizedException("Token JWT inválido o usuario no encontrado");
        }
    }

    /**
     * 🔄 Cambiar estado de apertura del restaurante.
     */
    @Operation(summary = "Cambiar estado del restaurante", description = "Abre o cierra el restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para cambiar estado de este restaurante"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @PutMapping("/{id}/status")
    @CacheEvict(value = "userRestaurant", allEntries = true)
    public ResponseEntity<Restaurant> updateRestaurantStatus(
            @PathVariable @Min(1) Long id,
            @RequestHeader("Authorization") String jwt
    ) throws RestaurantNotFoundException, UnauthorizedException {
        try {
            User user = userService.findUserByJwt(jwt);

            log.info("Usuario {} ({}) cambiando estado de restaurante ID: {}",
                    user.getEmail(), user.getRole(), id);

            // Validación granular de permisos
            if ("RESTAURANT_OWNER".equals(user.getRole())) {
                validateRestaurantOwnership(user, id);
            }

            Restaurant restaurant = restaurantService.updateRestaurantStatus(id);

            log.info("Estado de restaurante ID: {} cambiado a: {}",
                    id, restaurant.isOpen() ? "ABIERTO" : "CERRADO");
            return ResponseEntity.ok(restaurant);

        } catch (RestaurantNotFoundException | UnauthorizedException e) {
            throw e; // Re-lanzar excepciones específicas
        } catch (Exception e) {
            log.error("Error al cambiar estado de restaurante: {}", e.getMessage());
            throw new UnauthorizedException("Token JWT inválido o usuario no encontrado");
        }
    }

    /**
     * 🔍 Obtener restaurante asociado al usuario autenticado.
     */
    @Operation(summary = "Obtener restaurante propio", description = "Devuelve el restaurante del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no tiene restaurante asociado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT_OWNER')")
    @GetMapping("/user")
    @Cacheable(value = "userRestaurant", key = "#jwt")
    public ResponseEntity<Restaurant> getRestaurantByUserId(
            @RequestHeader("Authorization") String jwt
    ) throws RestaurantNotFoundException, UnauthorizedException {
        try {
            User user = userService.findUserByJwt(jwt);

            log.debug("Usuario {} obteniendo su restaurante", user.getEmail());

            Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());
            return ResponseEntity.ok(restaurant);

        } catch (RestaurantNotFoundException e) {
            throw e; // Re-lanzar excepción específica
        } catch (Exception e) {
            log.error("Error al obtener restaurante por usuario: {}", e.getMessage());
            throw new UnauthorizedException("Token JWT inválido o usuario no encontrado");
        }
    }



    // ==================== MÉTODOS PRIVADOS DE VALIDACIÓN ====================

    /**
     * Valida que el usuario sea propietario del restaurante especificado.
     */
    private void validateRestaurantOwnership(User user, Long restaurantId)
            throws RestaurantNotFoundException, UnauthorizedException {
        try {
            Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

            if (!restaurant.getOwner().getId().equals(user.getId())) {
                log.warn("Usuario {} intentó acceder al restaurante {} sin permisos",
                        user.getEmail(), restaurantId);
                throw new UnauthorizedException(
                        "No tienes permisos para acceder a este restaurante");
            }
        } catch (RestaurantNotFoundException e) {
            throw e; // Re-lanzar excepción específica
        } catch (Exception e) {
            log.error("Error al validar propiedad del restaurante: {}", e.getMessage());
            throw new RestaurantNotFoundException("Error al validar el restaurante");
        }
    }

    /**
     * Valida que un RESTAURANT_OWNER no tenga ya un restaurante.
     */
    private void validateSingleRestaurantOwnership(User user) throws UnauthorizedException {
        try {
            Restaurant existingRestaurant = restaurantService.getRestaurantByUserId(user.getId());
            if (existingRestaurant != null) {
                log.warn("Usuario {} ya tiene un restaurante (ID: {})",
                        user.getEmail(), existingRestaurant.getId());
                throw new UnauthorizedException(
                        "Ya tienes un restaurante registrado. Un propietario solo puede tener un restaurante.");
            }
        } catch (RestaurantNotFoundException e) {
            // Usuario no tiene restaurante, puede crear uno
            log.debug("Usuario {} no tiene restaurante previo, puede crear uno", user.getEmail());
        } catch (UnauthorizedException e) {
            throw e; // Re-lanzar excepción específica
        } catch (Exception e) {
            log.error("Error al validar propiedad única de restaurante: {}", e.getMessage());
            throw new UnauthorizedException("Error al validar la propiedad del restaurante");
        }
    }
}