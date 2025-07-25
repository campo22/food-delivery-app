package com.diver.service;

import com.diver.dto.IngredientCategoryDto;
import com.diver.dto.IngredientItemDto;
import com.diver.model.User;

import java.util.List;

/**
 * Contrato para el servicio de gestión de Ingredientes.
 * Define operaciones para las categorías de ingredientes y los ingredientes individuales,
 * asegurando la validación de permisos y la coherencia de los datos.
 */
public interface IngredientsService {

    /**
     * Crea una nueva categoría de ingredientes para un restaurante.
     * La implementación debe validar que el 'user' es el propietario del 'restaurantId'.
     *
     * @param name         El nombre de la nueva categoría.
     * @param restaurantId El ID del restaurante al que pertenecerá.
     * @param user         El usuario que realiza la acción.
     * @return El DTO de la categoría creada.
     */
    IngredientCategoryDto createIngredientCategory(String name, Long restaurantId, User user);

    /**
     * Encuentra una categoría de ingredientes por su ID.
     *
     * @param id El ID de la categoría a buscar.
     * @return El DTO de la categoría encontrada.

     */
    IngredientCategoryDto findIngredientCategoryById(Long id);

    /**
     * Encuentra todas las categorías de ingredientes de un restaurante.
     *
     * @param restaurantId El ID del restaurante.
     * @return Una lista de DTOs de las categorías.
     */
    List<IngredientCategoryDto> findIngredientCategoriesByRestaurantId(Long restaurantId);

    /**
     * Crea un nuevo ingrediente y lo asocia a una categoría y un restaurante.
     * La implementación debe validar que el 'user' es el propietario del 'restaurantId'.
     *
     * @param name         El nombre del nuevo ingrediente.
     * @param restaurantId El ID del restaurante al que pertenecerá.
     * @param categoryId   El ID de la categoría a la que pertenecerá.
     * @param user         El usuario que realiza la acción.
     * @return El DTO del ingrediente creado.
     */
    IngredientItemDto createIngredientItem(String name, Long restaurantId, Long categoryId, User user);

    /**
     * Encuentra todos los ingredientes de un restaurante.
     *
     * @param restaurantId El ID del restaurante.
     * @return Una lista de DTOs de los ingredientes.
     */
    List<IngredientItemDto> findIngredientsItemsByRestaurantId(Long restaurantId);

    /**
     * Actualiza el estado de stock de un ingrediente.
     * La implementación debe validar que el 'user' es el propietario.
     *
     * @param ingredientId El ID del ingrediente a actualizar.
     * @param user         El usuario que realiza la acción.
     * @return El DTO del ingrediente con su estado actualizado.
     */
    IngredientItemDto updateStock(Long ingredientId, User user);
}