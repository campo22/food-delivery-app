package com.diver.controller;

import com.diver.dto.IngredientCategoryDto;
import com.diver.dto.IngredientItemDto;
import com.diver.model.User;
import com.diver.request.IngredientCategoryRequest;
import com.diver.request.IngredientItemRequest;
import com.diver.service.IngredientsService;
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

import java.util.List;

/**
 * Controlador REST para la gestión de ingredientes y sus categorías por parte
 * de administradores y propietarios de restaurantes.
 * <p>
 * Este controlador unifica todas las operaciones de escritura y lectura privilegiada
 * relacionadas con el inventario de ingredientes.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/ingredients")
@Tag(
        name = "Admin: Ingredients Management",
        description = "Endpoints para la gestión de ingredientes y sus categorías."
)
@SecurityRequirement(name = "bearerAuth")
public class IngredientController {

    private final IngredientsService ingredientsService;

    // --- Endpoints para CATEGORÍAS de Ingredientes ---

    /**
     * Crea una nueva categoría de ingredientes para un restaurante específico.
     * @param request DTO con el nombre de la categoría y el ID del restaurante.
     * @param user El usuario autenticado que realiza la acción.
     * @return ResponseEntity con el DTO de la categoría creada y estado 201 Created.
     */
    @PostMapping("/category")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(
            summary = "Crear una categoría de ingrediente",
            description = "Permite a un propietario de restaurante crear una nueva " +
                          "categoría para sus ingredientes (ej: Lácteos, Vegetales)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientCategoryDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado (no es propietario)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<IngredientCategoryDto> createIngredientCategory(
            @Valid @RequestBody IngredientCategoryRequest request,
            @AuthenticationPrincipal User user
    ) {
        IngredientCategoryDto response = ingredientsService.createIngredientCategory(
                request.getName(), request.getRestaurantId(), user
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene todas las categorías de ingredientes de un restaurante.
     * @param restaurantId El ID del restaurante.
     * @return ResponseEntity con la lista de DTOs de las categorías.
     */
    @GetMapping("/category/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(
            summary = "Listar categorías de un restaurante",
            description = "Devuelve todas las categorías de ingredientes para un restaurante específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorías listadas exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content
            )
    })
        public ResponseEntity<List<IngredientCategoryDto>> getIngredientCategoriesByRestaurantId(
            @Parameter(description = "ID del restaurante", required = true)
            @PathVariable Long restaurantId) {
        List<IngredientCategoryDto> categories = ingredientsService.findIngredientCategoriesByRestaurantId(
                restaurantId
        );
        return ResponseEntity.ok(categories);
    }

    /**
     * Obtiene una categoría de ingredientes por su ID.
     * @param categoryId El ID de la categoría.
     * @return ResponseEntity con el DTO de la categoría.
     */
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(
            summary = "Obtener una categoría de ingrediente",
            description = "Devuelve una categoría de ingrediente por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría obtenida exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content
            )
    })
    public ResponseEntity<IngredientCategoryDto> getIngredientCategoryById(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long categoryId) {
        IngredientCategoryDto category = ingredientsService.findIngredientCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }


    // --- Endpoints para ÍTEMS de Ingredientes ---

    /**
     * Crea un nuevo ingrediente.
     * @param request DTO con los detalles del nuevo ingrediente.
     * @param user El usuario autenticado.
     * @return ResponseEntity con el DTO del ingrediente creado y estado 201 Created.
     */
    @PostMapping("/item")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(
            summary = "Crear un nuevo ingrediente",
            description = "Añade un nuevo ítem de ingrediente al inventario de un restaurante."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ingrediente creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientItemDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante o categoría no encontrados",
                    content = @Content
            )
    })
    public ResponseEntity<IngredientItemDto> createIngredientItem(
            @Valid @RequestBody IngredientItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        IngredientItemDto response = ingredientsService.createIngredientItem(
                request.getName(), request.getRestaurantId(), request.getCategoryId(), user
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene todos los ingredientes de un restaurante.
     * @param restaurantId El ID del restaurante.
     * @return ResponseEntity con la lista de DTOs de los ingredientes.
     */
    @GetMapping("/item/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(
            summary = "Listar ingredientes de un restaurante",
            description = "Devuelve todos los ítems de ingredientes para un restaurante específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ingredientes listados exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurante no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<List<IngredientItemDto>> getIngredientItemsByRestaurantId(
            @Parameter(description = "ID del restaurante", required = true)
            @PathVariable Long restaurantId) {
        List<IngredientItemDto> items = ingredientsService.findIngredientsItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(items);
    }

    /**
     * Actualiza el estado de stock de un ingrediente (en stock / sin stock).
     * @param ingredientId El ID del ingrediente a actualizar.
     * @param user El usuario autenticado.
     * @return ResponseEntity con el DTO del ingrediente actualizado.
     */
    @PutMapping("/item/{ingredientId}/stock")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(
            summary = "Actualizar estado de stock de un ingrediente",
            description = "Alterna el estado de disponibilidad (en stock / sin stock) de un ingrediente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientItemDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ingrediente no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<IngredientItemDto> updateIngredientStock(
            @Parameter(description = "ID del ingrediente a actualizar")
            @PathVariable Long ingredientId,
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' solicita actualizar stock del ingrediente ID: {}", user.getEmail(), ingredientId);
        IngredientItemDto response = ingredientsService.updateStock(ingredientId, user);
        return ResponseEntity.ok(response);
    }
}