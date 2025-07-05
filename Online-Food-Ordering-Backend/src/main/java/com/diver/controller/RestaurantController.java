package com.diver.controller;

import com.diver.dto.AdddToFavoritesDto;

import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para las interacciones públicas y de usuario con los restaurantes.
 * <p>
 * Proporciona endpoints para que los usuarios autenticados puedan buscar, listar,
 * ver detalles y gestionar sus restaurantes favoritos. A diferencia de {@link AdminRestaurantController},
 * este controlador expone operaciones de solo lectura y acciones específicas del usuario.
 *
 * @author Tu Nombre (o el nombre del equipo)
 * @version 1.0
 * @since 2024-06-27
 */
@Slf4j
@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
@Tag(
        name = "User: Restaurant Operations",
        description = "Endpoints para la interacción de usuarios con los restaurantes.")
@SecurityRequirement(name = "bearerAuth") // Todos los endpoints aquí requieren autenticación
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * Busca restaurantes basados en una palabra clave.
     * <p>
     * Permite a los usuarios encontrar restaurantes cuyo nombre o tipo de cocina
     * coincidan con el término de búsqueda proporcionado.
     *
     * @param keyword La palabra clave para filtrar los restaurantes.
     * @return un {@link ResponseEntity} con una lista de restaurantes que coinciden y un estado HTTP 200 (OK).
     */
    @Operation(summary = "Buscar restaurantes", description = "Busca restaurantes por nombre o tipo de cocina.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurants(
            @Parameter(description = "Palabra clave para la búsqueda", required = true, example = "pizza")
            @RequestParam("keyword") String keyword
    ) {
        log.debug("Buscando restaurantes con la palabra clave: '{}'", keyword);
        List<Restaurant> restaurants = restaurantService.searchRestaurants(keyword);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Obtiene una lista de todos los restaurantes disponibles en el sistema.
     *
     * @return un {@link ResponseEntity} con la lista completa de restaurantes y un estado HTTP 200 (OK).
     */
    @Operation(
            summary = "Listar todos los restaurantes",
            description = "Devuelve una lista de todos los restaurantes registrados.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de restaurantes obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        log.debug("Solicitud para obtener todos los restaurantes.");
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Busca y devuelve un restaurante por su ID único.
     *
     * @param id El ID del restaurante a buscar.
     * @return un {@link ResponseEntity} con el restaurante encontrado y un estado HTTP 200 (OK).
     * @throws com.diver.exception.RestaurantNotFoundException si no se encuentra el restaurante.
     */
    @Operation(summary = "Obtener un restaurante por ID",
            description = "Devuelve los detalles de un restaurante específico.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurante encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> findRestaurantById(
            @Parameter(description = "ID del restaurante a obtener", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.debug("Solicitud para obtener el restaurante con ID: {}", id);
        Restaurant restaurant = restaurantService.findRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    /**
     * Agrega un restaurante a la lista de favoritos del usuario autenticado.
     * <p>
     * Esta operación es idempotente en su efecto: si el restaurante ya es favorito, no se produce ningún cambio.
     *
     * @param user El usuario autenticado, inyectado por Spring Security.
     * @param id El ID del restaurante a añadir como favorito.
     * @return un {@link ResponseEntity} con un DTO del restaurante añadido y un estado HTTP 200 (OK).
     * @throws com.diver.exception.RestaurantNotFoundException si el restaurante no existe.
     * @throws com.diver.exception.OperationNotAllowedException si el restaurante ya está en favoritos.
     */
    @Operation(summary = "Añadir un restaurante a favoritos",
            description = "Marca un restaurante como favorito para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurante añadido a favoritos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =AdddToFavoritesDto.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: el restaurante ya es un favorito",
                    content = @Content)
    })
    @PutMapping("/{id}/add-favorite")
    public ResponseEntity<AdddToFavoritesDto> addToFavorite(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID del restaurante a marcar como favorito", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("Usuario '{}' añadiendo restaurante con ID {} a favoritos.", user.getEmail(), id);
        AdddToFavoritesDto restaurantDto = restaurantService.addToFavorite(id, user);
        return ResponseEntity.ok(restaurantDto);
    }
}