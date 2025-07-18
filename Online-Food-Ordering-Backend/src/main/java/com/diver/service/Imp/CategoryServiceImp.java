package com.diver.service.Imp;

import com.diver.dto.CategoryDto;
import com.diver.exception.OperationNotAllowedException;
import com.diver.exception.RestaurantNotFoundException;
import com.diver.model.Category;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.repository.CategoryRepository;
import com.diver.repository.RestaurantRepository;
import com.diver.service.CategoryService;
import com.diver.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImp implements CategoryService {

    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Crea una nueva categoría para el restaurante propiedad del usuario autenticado.
     * <p>
     * La implementación debe verificar que el usuario es un RESTAURANT_OWNER y
     * asociar la nueva categoría a su restaurante.
     *
     * @param name El nombre de la nueva categoría (ej: "Pizzas", "Postres").
     * @param user El usuario autenticado (propietario del restaurante).
     * @return El DTO de la categoría recién creada.
     * @throws OperationNotAllowedException si el usuario no es propietario.
     */
    @Transactional
    @Override
    public CategoryDto createCategory(String name, User user) {

        log.info("Usuario '{}' solicita crear una nueva categoría: '{}'.", user.getEmail(), name);
        // 1. Validamos que el usuario sea propietario del restaurante
        Restaurant restaurant= restaurantRepository.findByOwnerId(user.getId());
       if ( restaurant== null){
           throw new RestaurantNotFoundException("No se puede crear la categoria, el usuario no es propietarios");
       }

        // 2. Creamos la nueva categoría
        Category category = new Category();
        category.setName(name);
        category.setRestaurant(restaurant);

        Category savedCategory = categoryRepository.save(category);

        log.info("Categoría '{}' (ID: {}) creada exitosamente para el restaurante '{}'.",
                savedCategory.getName(), savedCategory.getId(), restaurant.getName());

        // 3. Devolvemos el DTO de la nueva categoría
        return mapToCategoryDto(category);



    }

    /**
     * Encuentra todas las categorías asociadas a un restaurante específico.
     *
     * @param restaurantId El ID del restaurante.
     * @return Una lista de DTOs de las categorías encontradas.
     */

    @Transactional ( readOnly = true)
    @Override
    public List<CategoryDto> findCategoriesByRestaurantId(Long restaurantId) {

        if (!restaurantRepository.existsById( restaurantId)) {
            throw new RestaurantNotFoundException("El restaurante con ID " + restaurantId + " no fue encontrado.");
        }

        List<Category> categories= categoryRepository.findByRestaurantId(restaurantId);

        return categories.stream().map(this::mapToCategoryDto).collect(Collectors.toList());
    }

    /**
     * Encuentra una categoría por su ID único.
     *
     * @param id El ID de la categoría a buscar.
     * @return El DTO de la categoría encontrada.
     */
    @Override
    @Transactional ( readOnly = true)
    public CategoryDto findCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Categoría no encontrada con ID: " + id));
        return mapToCategoryDto(category);
    }

    // --- MÉTODO PRIVADO DE MAPEADO ---

    /**
     * Convierte una entidad Category a su correspondiente CategoryDto.
     *
     * @param category La entidad a convertir.
     * @return El DTO resultante.
     */
    private CategoryDto mapToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getRestaurant() != null) {
            dto.setRestaurantId(category.getRestaurant().getId());
        }

        return dto;
    }
}
