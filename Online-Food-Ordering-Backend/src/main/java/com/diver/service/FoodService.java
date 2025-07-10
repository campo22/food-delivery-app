package com.diver.service;

import com.diver.dto.FoodDto; // Asumiendo que crearás este DTO
import com.diver.model.User;
import com.diver.request.CreateFoodRequest; // Nombre de clase corregido

import java.util.List;

/**
 * Contrato para el servicio de gestión de platos de comida (Food).
 * Define las operaciones de negocio, asegurando la validación, seguridad y
 * el desacoplamiento de la capa de persistencia mediante el uso de DTOs.
 */
public interface FoodService {

    /**
     * Crea un nuevo plato de comida y lo asocia a un restaurante y una categoría.
     * La validación de propiedad (el usuario debe ser el dueño del restaurante)
     * se realiza dentro de la implementación.
     *
     * @param req          El DTO con los datos del nuevo plato.
     * @param categoryId   El ID de la categoría a la que pertenece el plato.
     * @param restaurantId El ID del restaurante que ofrece el plato.
     * @param user         El usuario (propietario) que realiza la creación.
     * @return El DTO del plato recién creado.
     */
    FoodDto createFood(CreateFoodRequest req, Long categoryId, Long restaurantId, User user);

    /**
     * Elimina un plato de comida por su ID.
     * La implementación debe validar que el usuario que realiza la acción
     * es el propietario del restaurante al que pertenece el plato.
     *
     * @param foodId El ID del plato a eliminar.
     * @param user   El usuario que solicita la eliminación.
     * @throws com.diver.exception.FoodNotFoundException si el plato no existe.
     * @throws com.diver.exception.AccessDeniedException si el usuario no tiene permisos.
     */
    void deleteFood(Long foodId, User user);

    /**
     * Obtiene una lista de platos de un restaurante, con filtros opcionales.
     *
     * @param restaurantId   El ID del restaurante.
     * @param isVegetarian   Filtra por platos vegetarianos.
     * @param isNonVeg       Filtra por platos no vegetarianos.
     * @param isSeasonal     Filtra por platos de temporada.
     * @param foodCategory   Filtra por el nombre de una categoría específica.
     * @return Una lista de DTOs de los platos que coinciden con los filtros.
     */
    List<FoodDto> getRestaurantFoods(
            Long restaurantId,
            boolean isVegetarian,
            boolean isNonVeg,
            boolean isSeasonal,
            String foodCategory
    );

    /**
     * Busca platos en toda la aplicación por una palabra clave.
     *
     * @param keyword La palabra clave para buscar en nombres o descripciones.
     * @return Una lista de DTOs de los platos encontrados.
     */
    List<FoodDto> searchFood(String keyword); // Renombrado a singular para consistencia

    /**
     * Encuentra un plato por su ID.
     *
     * @param foodId El ID del plato a buscar.
     * @return El DTO del plato encontrado.
     * @throws com.diver.exception.FoodNotFoundException si el plato no existe.
     */
    FoodDto findFoodById(Long foodId);

    /**
     * Actualiza el estado de disponibilidad de un plato (disponible/no disponible).
     *
     * @param foodId El ID del plato a actualizar.
     * @param user   El usuario que realiza la acción, para validación de propiedad.
     * @return El DTO del plato con su estado actualizado.
     * @throws com.diver.exception.FoodNotFoundException si el plato no existe.
     * @throws com.diver.exception.AccessDeniedException si el usuario no tiene permisos.
     */
    FoodDto updateAvailabilityStatus(Long foodId, User user);
}