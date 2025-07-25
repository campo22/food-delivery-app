package com.diver.service.Imp;

import com.diver.dto.IngredientCategoryDto;
import com.diver.dto.IngredientItemDto;
import com.diver.exception.AccessDeniedException;
import com.diver.exception.RestaurantNotFoundException;
import com.diver.model.IngredientCategory;
import com.diver.model.IngredientItem;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.repository.IngredientCategoryRepository;
import com.diver.repository.IngredientItemRepository;
import com.diver.repository.RestaurantRepository;
import com.diver.service.IngredientsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceImp implements IngredientsService  {

    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final IngredientItemRepository ingredientItemRepository;
    private final RestaurantRepository restaurantRepository;


    /**
     * Crea una nueva categoría de ingredientes para un restaurante.
     * La implementación debe validar que el 'user' es el propietario del 'restaurantId'.
     *
     * @param name         El nombre de la nueva categoría.
     * @param restaurantId El ID del restaurante al que pertenecerá.
     * @param user         El usuario que realiza la acción.
     * @return El DTO de la categoría creada.
     */
    @Override
    @Transactional
    public IngredientCategoryDto createIngredientCategory(String name, Long restaurantId, User user) {

        log.info( "Usuario '{}' solicita crear una nueva categoría de ingredientes para el restaurante con ID '{}'.",
                user.getEmail(),
                restaurantId);

        Restaurant restaurant= findRestaurantById( restaurantId);
        validateRestaurantOwnership(restaurant, user);

        IngredientCategory ingredientCategory = new IngredientCategory();
        ingredientCategory.setName(name);
        ingredientCategory.setRestaurant(restaurant);

         IngredientCategory savedCategory =ingredientCategoryRepository.save(ingredientCategory);
         log.info( "Categoría de ingredientes '{}' (ID: {}) creada exitosamente '.",
                 savedCategory.getName(),
                 savedCategory.getId());

        return mapToIngredientCategoryDto(savedCategory);
    }

    /**
     * Encuentra una categoría de ingredientes por su ID.
     *
     * @param id El ID de la categoría a buscar.
     * @return El DTO de la categoría encontrada.
     */
    @Override
    @Transactional(readOnly = true)
    public IngredientCategoryDto findIngredientCategoryById(Long id) {
        log.info("Buscando categoría de ingredientes con ID: {}", id);

        IngredientCategory category= ingredientCategoryRepository.findById( id)
                .orElseThrow(() -> new ResourceAccessException("Categoría de ingredientes no encontrada con ID: " + id));
        return mapToIngredientCategoryDto(category);
    }

    /**
     * Encuentra todas las categorías de ingredientes de un restaurante.
     *
     * @param restaurantId El ID del restaurante.
     * @return Una lista de DTOs de las categorías.
     */
    @Override
    @Transactional(readOnly = true)
    public List<IngredientCategoryDto> findIngredientCategoriesByRestaurantId(Long restaurantId) {

        log.info("Buscando categorías de ingredientes para el restaurante con ID: {}", restaurantId);
        Restaurant restaurant = findRestaurantById(restaurantId);

        List<IngredientCategory> categories = ingredientCategoryRepository.findByRestaurantId(restaurantId);

        return categories.stream()
                .map(this::mapToIngredientCategoryDto)
                .collect(Collectors.toList());
    }

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
    @Override
    @Transactional
    public IngredientItemDto createIngredientItem(String name, Long restaurantId, Long categoryId, User user) {

        log.info( "Usuario '{}' solicita crear un nuevo ingrediente para el restaurante con ID '{}'.",
                user.getEmail(),
                restaurantId);
        Restaurant restaurant = findRestaurantById(restaurantId);
        validateRestaurantOwnership(restaurant, user);

        IngredientCategory category =ingredientCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceAccessException("Categoría de ingredientes no encontrada con ID: " + categoryId));

        if (!category.getRestaurant().getId().equals(restaurant.getId())) {
                throw new ResourceAccessException("La categoría de ingredientes con ID " + categoryId + " " +
                        "no pertenece al restaurante con ID " + restaurantId);
            }

            IngredientItem ingredientItem = new IngredientItem();
            ingredientItem.setName(name);
            ingredientItem.setCategory(category);
            ingredientItem.setRestaurant(restaurant);
            ingredientItem.setInStock(true);

            IngredientItem savedItem = ingredientItemRepository.save(ingredientItem);
            log.info("Ingrediente '{}' (ID: {}) creado exitosamente para el restaurante con ID '{}'.",
                    savedItem.getName(),
                    savedItem.getId(),
                    restaurantId);

            return mapToIngredientItemDto(savedItem);

    }

    /**
     * Encuentra todos los ingredientes de un restaurante.
     *
     * @param restaurantId El ID del restaurante.
     * @return Una lista de DTOs de los ingredientes.
     */
    @Override
    @Transactional(readOnly = true)
    public List<IngredientItemDto> findIngredientsItemsByRestaurantId(Long restaurantId) {

        log.info("Buscando ingredientes para el restaurante con ID: {}", restaurantId);

        List<IngredientItem>item = ingredientItemRepository.findByRestaurantId(restaurantId);

        return item.stream()
                .map(this::mapToIngredientItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de stock de un ingrediente.
     * La implementación debe validar que el 'user' es el propietario.
     *
     * @param ingredientId El ID del ingrediente a actualizar.
     * @param user         El usuario que realiza la acción.
     * @return El DTO del ingrediente con su estado actualizado.
     */
    @Override
    @Transactional
    public IngredientItemDto updateStock(Long ingredientId, User user) {

        log.info( "Usuario '{}' solicita actualizar el stock de un ingrediente con ID '{}'.",
                user.getEmail(),
                ingredientId);

        IngredientItem item = ingredientItemRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceAccessException("Ingrediente no encontrado con ID: " + ingredientId));

        validateRestaurantOwnership(item.getRestaurant(), user);

        item.setInStock(!item.isInStock());
        IngredientItem updatedItem = ingredientItemRepository.save(item);
        log.info("Stock de ingrediente '{}' (ID: {}) actualizado exitosamente para el restaurante con ID '{}'.",
                updatedItem.getName(),
                updatedItem.getId(),
                updatedItem.getRestaurant().getId());

        return mapToIngredientItemDto(updatedItem);

    }

    //metodo privados

    private Restaurant findRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurante no encontrado con ID: " + restaurantId));
    }

    private void validateRestaurantOwnership(Restaurant restaurant, User user) {

        if (user.getRole().name().equals("ROLE_ADMIN")) return;


        if (!restaurant.getOwner().getId().equals(user.getId())) {
            log.warn( " acceso denegado al usuario '{}', no es propietario del restaurante con ID '{}'.",
                    user.getEmail(),
                    restaurant.getId());
            throw new AccessDeniedException("El usuario no es propietario del restaurante");
        }
    }

    private IngredientCategoryDto mapToIngredientCategoryDto(IngredientCategory category) {
        if (category == null)return null;

        IngredientCategoryDto dto = new IngredientCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getRestaurant() != null) {
            dto.setRestaurantId(category.getRestaurant().getId());
        }
        return dto;
    }
    private IngredientItemDto mapToIngredientItemDto(IngredientItem item) {
        if (item == null)return null;

        IngredientItemDto dto = new IngredientItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setInStock(item.isInStock());
        dto.setCategory(mapToIngredientCategoryDto(item.getCategory()));

        return dto;
    }


}
