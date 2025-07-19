package com.diver.controller;

import com.diver.dto.FoodDto;
import com.diver.model.Food;
import com.diver.model.User;
import com.diver.request.CreateFoodRequest;
import com.diver.response.MessageResponse;
import com.diver.service.FoodService;
import com.diver.service.RestaurantService;
import com.diver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * Controlador REST para la gestión de platos (Food) por parte de administradores y
 * propietarios de restaurantes.
 * <p>
 * Proporciona endpoints para crear, actualizar y eliminar platos del menú.
 * La seguridad se aplica a nivel de método para garantizar que solo los usuarios
 * autorizados puedan realizar cambios.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/food")
@RequiredArgsConstructor
@Tag(name = "Admin: Food Management",
        description = "Endpoints para la gestión de platos por parte de los propietarios.")
@SecurityRequirement(name = "bearerAuth")
public class AdminFoodController {

    private final FoodService foodService;
    private final UserService userService; // Necesario para obtener el usuario completo
    private final RestaurantService restaurantService; // Necesario para obtener el restaurante del usuario

    /**
     * Crea un nuevo plato de comida para el restaurante del propietario autenticado.
     *
     * @param req  El DTO con los datos del nuevo plato.
     * @param user El usuario (propietario) autenticado.
     * @return Un ResponseEntity con el DTO del plato creado y un estado 201 Created.
     */
    @Operation(summary = "Crear un nuevo plato",
            description = "Añade un nuevo plato al menú del restaurante del propietario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es propietario)"),
            @ApiResponse(responseCode = "404", description = "Restaurante o categoría no encontrados")
    })
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<FoodDto> createFood(
            @Valid @RequestBody CreateFoodRequest req,
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' solicita la creación de un nuevo plato: '{}'", user.getEmail(), req.getName());

        // Obtenemos el restaurante del propietario
        var restaurant = restaurantService.getRestaurantByUserId(user.getId());

        // El DTO de request debería tener el categoryId
        FoodDto createdFood = foodService.createFood(req, req.getCategoryId(), restaurant.getId(), user);

        return new ResponseEntity<>(createdFood, HttpStatus.CREATED);
    }

    /**
     * Elimina un plato de comida por su ID.
     *
     * @param foodId El ID del plato a eliminar.
     * @param user   El usuario autenticado, para validación de propiedad.
     * @return Un ResponseEntity con estado 204 No Content si la eliminación es exitosa.
     */
    @Operation(summary = "Eliminar un plato", description = "Elimina un plato del menú por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plato eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es propietario)"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @DeleteMapping("/{foodId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteFood(
            @Parameter(description = "ID del plato a eliminar", required = true)
            @PathVariable Long foodId,
            @AuthenticationPrincipal User user
    ) {

     foodService.deleteFood(foodId, user);
        MessageResponse resp = new MessageResponse("Plato eliminado exitosamente", true, null );
        return new ResponseEntity<>( resp,HttpStatus.NO_CONTENT);
    }

    /**
     * Actualiza el estado de disponibilidad de un plato.
     *
     * @param foodId El ID del plato a actualizar.
     * @param user   El usuario autenticado, para validación de propiedad.
     * @return Un ResponseEntity con el DTO del plato actualizado y un estado 200 OK.
     */
    @Operation(summary = "Actualizar disponibilidad de un plato",
            description = "Marca un plato como disponible o no disponible en el menú.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es propietario)"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PutMapping("/{foodId}/availability")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<FoodDto> updateFoodAvailabilityStatus(
            @Parameter(description = "ID del plato a actualizar", required = true)
            @PathVariable Long foodId,
            @AuthenticationPrincipal User user
    ) {
        FoodDto updatedFood = foodService.updateAvailabilityStatus(foodId, user);
        return ResponseEntity.ok(updatedFood);
    }
}