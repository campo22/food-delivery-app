package com.diver.service.Imp;

import com.diver.dto.FoodDto;
import com.diver.exception.AccessDeniedException;
import com.diver.exception.FoodNotFoundException;
import com.diver.exception.RestaurantNotFoundException;
import com.diver.model.Category;
import com.diver.model.Food;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.repository.CategoryRepository;
import com.diver.repository.FoodRepository;
import com.diver.repository.RestaurantRepository;
import com.diver.request.CreateFoodRequest;
import com.diver.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodServiceImp implements FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;


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
    @Transactional
    @Override
    public FoodDto createFood(CreateFoodRequest req, Long categoryId, Long restaurantId, User user) {

        log.info("Iniciando creación de plato de comida para el restaurante con ID: {}",
                req.getName(),
                restaurantId );

        // Validar si el restaurante existe
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RestaurantNotFoundException(
                "El restaurante con ID " + restaurantId + " no fue encontrado."));

        // Validar si el usuario es el propietario del restaurante
        if (!restaurant.getOwner().getId().equals(user.getId()))  {
            log.warn("El usuario '{}' no es el propietario del restaurante con ID '{}'.",
                    user.getEmail(),
                    restaurantId);
            throw new AccessDeniedException(
                    "No tienes permiso para crear un plato de comida para este restaurante.");
        }

        // Validar si la categoría existe
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La categoría con ID " + categoryId + " no fue encontrada."));

        // crear el plato de comida
        Food newFood = new Food();
        newFood.setName(req.getName());
        newFood.setDescription(req.getDescription());
        newFood.setPrice(req.getPrice());
        newFood.setCategory(category);
        newFood.setImages(req.getImage());
        newFood.setAvailable(req.isAvailable());
        newFood.setRestaurant(restaurant);
        newFood.setVegetarian(req.isVegetarian());
        newFood.setSeasonal(req.isSeasonal());
        newFood.setCreationDate(new Date());

        // Guardar el plato de comida en la base de datos
        Food savedFood = foodRepository.save(newFood);
        log.info("Plato de comida creado con ID: {}",
                savedFood.getId(),
                savedFood.getName(),
                restaurant.getName());

        // Mapear el plato de comida a un DTO
        return mapToFoodDto(savedFood);
    }


    /**
     * Elimina un plato de comida por su ID.
     * La implementación debe validar que el usuario que realiza la acción
     * es el propietario del restaurante al que pertenece el plato.
     *
     * @param foodId El ID del plato a eliminar.
     * @param user   El usuario que solicita la eliminación.
     * @throws FoodNotFoundException si el plato no existe.
     * @throws AccessDeniedException si el usuario no tiene permisos.
     */
    @Transactional
    @Override
    public void deleteFood(Long foodId, User user) {

        log.info("Iniciando eliminación de plato de comida con ID: {}", foodId);

        // Validacion y seguridad
        Food food= findFoodByIdAndValidateOwnership(foodId, user);

        // Eliminar el plato de comida
        foodRepository.delete(food);
        log.info("Plato de comida eliminado con ID: {}", foodId);

    }

    /**
     * Obtiene una lista de platos de un restaurante, con filtros opcionales.
     *
     * @param restaurantId El ID del restaurante.
     * @param isVegetarian Filtra por platos vegetarianos.
     * @param isNonVeg     Filtra por platos no vegetarianos.
     * @param isSeasonal   Filtra por platos de temporada.
     * @param foodCategory Filtra por el nombre de una categoría específica.
     * @return Una lista de DTOs de los platos que coinciden con los filtros.
     */
    @Override
    public List<FoodDto> getRestaurantFoods(Long restaurantId,
                                            Boolean isVegetarian,
                                            Boolean isNonVeg,
                                            Boolean isSeasonal,
                                            String foodCategory) {

        // buscar el restaurante por id en la base de datos
        List<Food> foods= foodRepository.findByRestaurantId(restaurantId);

        log.info("El repositorio encontró {} platos para el restaurante ID {}.", foods.size(), restaurantId);

        log.debug("Obteniendo platos para el restaurante ID {}. Filtros: " +
                        "isVeg={}, isNonVeg={}, isSeasonal={}, category='{}'",
                restaurantId, isVegetarian, isNonVeg, isSeasonal, foodCategory);


        var foodStream = foods.stream();


        // Filtro vegetariano/no vegetariano. Son mutuamente excluyentes.
        if (isVegetarian != null) {
            log.debug("Aplicando filtro vegetariano: {}", isVegetarian);
            foodStream = foodStream.filter(food -> food.isVegetarian() == isVegetarian);
        } else if (isNonVeg != null && isNonVeg) {
            log.debug("Aplicando filtro no vegetariano.");
            foodStream = foodStream.filter(food -> !food.isVegetarian());
        }

        // Filtro de temporada.
        if (isSeasonal != null) {
            log.debug("Aplicando filtro de temporada: {}", isSeasonal);
            foodStream = foodStream.filter(food -> food.isSeasonal() == isSeasonal);
        }

        // Filtro de categoría.
        if (foodCategory != null && !foodCategory.isEmpty()) {
            log.debug("Aplicando filtro de categoría: {}", foodCategory);
            foodStream = foodStream.filter(food -> food.getCategory() != null
                    && food.getCategory().getName().equalsIgnoreCase(foodCategory));
        }

        // 4. Recolectamos los resultados del stream filtrado.
        List<Food> filteredFoods = foodStream.collect(Collectors.toList());
        log.info("Después de aplicar filtros, quedaron {} platos.", filteredFoods.size());

        return mapToFoodDtoList(filteredFoods);
    }


    /**
     * Busca platos en toda la aplicación por una palabra clave.
     *
     * @param keyword La palabra clave para buscar en nombres o descripciones.
     * @return Una lista de DTOs de los platos encontrados.
     */
    @Override
    public List<FoodDto> searchFood(String keyword) {
        return foodRepository.searchFood( keyword).stream()
                .map(this::mapToFoodDto)
                .collect(Collectors.toList());
    }

    /**
     * Encuentra un plato por su ID.
     *
     * @param foodId El ID del plato a buscar.
     * @return El DTO del plato encontrado.
     * @throws FoodNotFoundException si el plato no existe.
     */
    @Override
    public FoodDto findFoodById(Long foodId) {
       Optional<Food> food= foodRepository.findById(foodId);
       if ( food.isEmpty()) {
           throw new FoodNotFoundException("Plato no encontrado con ID: " + foodId);
       }

       return mapToFoodDto( food.get());

    }

    /**
     * Actualiza el estado de disponibilidad de un plato (disponible/no disponible).
     *
     * @param foodId El ID del plato a actualizar.
     * @param user   El usuario que realiza la acción, para validación de propiedad.
     * @return El DTO del plato con su estado actualizado.
     * @throws FoodNotFoundException si el plato no existe.
     * @throws AccessDeniedException si el usuario no tiene permisos.
     */
    @Transactional
    @Override
    public FoodDto updateAvailabilityStatus(Long foodId, User user) {
        log.info("Usuario '{}' solicita cambiar estado de disponibilidad para el plato ID {}.",
                user.getEmail(), foodId);

        // 1. REUTILIZAMOS nuestra lógica de validación de propiedad.
        Food foodToUpdate = findFoodByIdAndValidateOwnership(foodId, user);

        // 2. LÓGICA DE NEGOCIO: Alternamos el estado.
        foodToUpdate.setAvailable(!foodToUpdate.isAvailable());

        // 3. PERSISTENCIA: Guardamos el cambio.
        // @Transactional se encargará del commit, pero save() es explícito.
        Food updatedFood = foodRepository.save(foodToUpdate);

        log.info("Estado de disponibilidad del plato '{}' (ID: {}) cambiado a: {}",
                updatedFood.getName(), updatedFood.getId(), updatedFood.isAvailable());

        // 4. RESPUESTA DTO: Devolvemos el estado actualizado.
        return mapToFoodDto(updatedFood);
    }

    // methods de mapeos a dto

    private FoodDto mapToFoodDto(Food food) {

        if (food== null)return null;

        FoodDto foodDto= new FoodDto();
        foodDto.setId(food.getId());
        foodDto.setName(food.getName());
        foodDto.setDescription(food.getDescription());
        foodDto.setPrice(food.getPrice());
        foodDto.setImages(food.getImages());
        foodDto.setAvailable(food.isAvailable());
        foodDto.setVegetarian(food.isVegetarian());
        foodDto.setSeasonal(food.isSeasonal());
        foodDto.setIngredients(food.getIngredients());
        foodDto.setCreationDate(food.getCreationDate());

        if (food.getCategory()!= null){
            FoodDto.CategoryDto categoryDto= new FoodDto.CategoryDto();
            categoryDto.setId(food.getCategory().getId());
            categoryDto.setName(food.getCategory().getName());
            foodDto.setCategory(categoryDto);
        }

        if (food.getRestaurant()!= null){
            FoodDto.RestaurantSimpleDto restaurantSimpleDto= new FoodDto.RestaurantSimpleDto();
            restaurantSimpleDto.setId(food.getRestaurant().getId());
            restaurantSimpleDto.setName(food.getRestaurant().getName());
            foodDto.setRestaurant(restaurantSimpleDto);
        }
        return foodDto;
    }
    private List<FoodDto> mapToFoodDtoList(List<Food> foods) {
        return foods.stream().map(this::mapToFoodDto).collect(Collectors.toList());
    }


    // --- METODO PRIVADO DE UTILIDAD PARA VALIDACIÓN ---
    /**
     * Busca un plato por su ID y valida que el usuario proporcionado tenga
     * permisos para modificarlo (es el propietario del restaurante o un ADMIN).
     * <p>
     * Este metodo centraliza la lógica de seguridad y validación para las operaciones
     * de actualización y eliminación de platos.
     *
     * @param foodId El ID del plato a buscar y validar.
     * @param user   El usuario cuyos permisos se van a verificar.
     * @return La entidad {@link Food} si se encuentra y la validación es exitosa.
     * @throws FoodNotFoundException si el plato no existe.
     * @throws AccessDeniedException si el usuario no tiene los permisos requeridos.
     */
    private Food findFoodByIdAndValidateOwnership(Long foodId, User user) {
        // Buscar el plato por su ID
        Food food = foodRepository.findById( foodId)
                .orElseThrow(()-> new FoodNotFoundException("Plato no encontrado con ID: " + foodId));

        // obtener el restaurante asociado al plato
        Restaurant restaurant= food.getRestaurant();

        // Validar si el usuario es el propietario del restaurante
        if(!restaurant.getOwner().getId().equals(user.getId()) && !user.getRole().name().equals("ROLE_ADMIN")){
            log.warn("ACESSO DENEGADO: El usuario '{}' no es el propietario del restaurante con ID '{}'.",
                    user.getEmail(),
                    restaurant.getId());
            throw new AccessDeniedException("El usuario no tiene permisos para realizar esta operación.");
        }

        return food;
    }

}
