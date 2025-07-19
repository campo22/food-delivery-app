package com.diver.controller;

import com.diver.dto.FoodDto;
import com.diver.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para las interacciones públicas con los platos de comida.
 * <p>
 * Proporciona endpoints de solo lectura para que cualquier visitante o usuario
 * pueda buscar platos, ver los menús de los restaurantes y consultar los
 * detalles de un plato específico.
 */
@Slf4j
@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
@Tag(name = "Public: Food Operations",
        description = "Endpoints públicos para consultar platos de comida.")
public class FoodController {

    private final FoodService foodService;

    /**
     * Busca platos en toda la plataforma por una palabra clave.
     *
     * @param keyword La palabra clave para buscar en el nombre o categoría del plato.
     * @return Un ResponseEntity con una lista de DTOs de los platos encontrados.
     */
    @Operation(
            summary = "Buscar platos",
            description = "Busca platos en todos los restaurantes por una palabra clave."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda exitosa")
    })
    @GetMapping("/search")
    public ResponseEntity<List<FoodDto>> searchFood(
            @Parameter(description = "Palabra clave para la búsqueda", required = true, example = "pizza")
            @RequestParam String keyword
    ) {
        log.debug("Búsqueda pública de platos con la palabra clave: '{}'", keyword);
        List<FoodDto> foods = foodService.searchFood(keyword);
        return ResponseEntity.ok(foods);
    }

    /**
     * Obtiene los platos de un restaurante específico, con opciones de filtrado.
     *
     * @param restaurantId El ID del restaurante cuyo menú se desea ver.
     * @param vegetarian   Filtro opcional para platos vegetarianos.
     * @param seasonal     Filtro opcional para platos de temporada.
     * @param nonveg       Filtro opcional para platos no vegetarianos.
     * @param food_category Filtro opcional por nombre de categoría.
     * @return Un ResponseEntity con la lista de platos filtrada.
     */
    @Operation
            (summary = "Obtener menú de un restaurante",
            description = "Devuelve la lista de platos de un restaurante, con filtros opcionales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menú obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodDto>> getRestaurantFood(
            @Parameter(description = "ID del restaurante", required = true, example = "1")
            @PathVariable Long restaurantId,

            @Parameter(description = "Filtrar por platos vegetarianos")
            @RequestParam(required = false) Boolean vegetarian,

            @Parameter(description = "Filtrar por platos de temporada")
            @RequestParam(required = false) Boolean seasonal,

            @Parameter(description = "Filtrar por platos no vegetarianos")
            @RequestParam(required = false) Boolean nonveg,

            @Parameter(description = "Filtrar por nombre de categoría", example = "Pizzas")
            @RequestParam(required = false) String food_category
    ) {
        log.debug("Solicitud de menú para el restaurante ID: {}", restaurantId);
        List<FoodDto> foods = foodService.getRestaurantFoods(restaurantId, vegetarian, nonveg, seasonal, food_category);
        return ResponseEntity.ok(foods);
    }

    /**
     * Obtiene los detalles de un plato de comida específico por su ID.
     *
     * @param id El ID del plato a buscar.
     * @return Un ResponseEntity con el DTO del plato.
     */
    @Operation(
            summary = "Obtener detalles de un plato",
            description = "Devuelve la información de un plato específico por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plato encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FoodDto> findFoodById(
            @Parameter(description = "ID del plato a buscar", required = true, example = "101")
            @PathVariable Long id
    ) {
        log.debug("Solicitud de detalles para el plato ID: {}", id);
        FoodDto food = foodService.findFoodById(id);
        return ResponseEntity.ok(food);
    }
}