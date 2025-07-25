package com.diver.service;

import com.diver.dto.CategoryDto;
import com.diver.model.User;

import java.util.List;

/**
 * Contrato para el servicio de gestión de Categorías de platos.
 * Define las operaciones de negocio para crear y consultar categorías
 * asociadas a un restaurante.
 */
public interface CategoryService {

    /**
     * Crea una nueva categoría para el restaurante propiedad del usuario autenticado.
     * <p>
     * La implementación debe verificar que el usuario es un RESTAURANT_OWNER y
     * asociar la nueva categoría a su restaurante.
     *
     * @param name El nombre de la nueva categoría (ej: "Pizzas", "Postres").
     * @param user El usuario autenticado (propietario del restaurante).
     * @return El DTO de la categoría recién creada.
     * @throws com.diver.exception.OperationNotAllowedException si el usuario no es propietario.
     */
    CategoryDto createCategory(String name, User user);

    /**
     * Encuentra todas las categorías asociadas a un restaurante específico.
     *
     * @param restaurantId El ID del restaurante.
     * @return Una lista de DTOs de las categorías encontradas.
     */
    List<CategoryDto> findCategoriesByRestaurantId(Long restaurantId);

    /**
     * Encuentra una categoría por su ID único.
     *
     * @param id El ID de la categoría a buscar.
     * @return El DTO de la categoría encontrada.
     *
     */
    CategoryDto findCategoryById(Long id);

    /**
     * Encuentra todas las categorías asociadas a un usuario.
     *
     * @param userId El ID del usuario.
     * @return Una lista de DTOs de las categorías encontradas.
     */
    List<CategoryDto> findCategoriesByUserId(Long userId);

}