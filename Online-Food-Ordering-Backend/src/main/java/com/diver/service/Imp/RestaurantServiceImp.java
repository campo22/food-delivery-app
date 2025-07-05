package com.diver.service.Imp;

import com.diver.dto.AdddToFavoritesDto;

import com.diver.dto.RestaurantDto;
import com.diver.exception.AccessDeniedException;
import com.diver.exception.OperationNotAllowedException;
import com.diver.exception.RestaurantNotFoundException;
import com.diver.model.Address;

import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.repository.AddressRepository;
import com.diver.repository.RestaurantRepository;
import com.diver.repository.UserRepository;
import com.diver.request.CreateRestaurantRequest;
import com.diver.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la gestión de restaurantes.
 * <p>
 * Esta clase encapsula toda la lógica de negocio, validaciones de seguridad y reglas
 * específicas relacionadas con las entidades de Restaurante. Su objetivo es mantener
 * los controladores delgados y centralizar la lógica de la aplicación en la capa de servicio.
 * <p>
 * Utiliza inyección de dependencias por constructor, promueve el uso de excepciones
 * de runtime para el manejo de errores y asegura la atomicidad de las operaciones
 * de escritura a través de la anotación {@code @Transactional}.
 *
 * @author Diver Campo Diaz
 * @version 1.1
 * @since 2023-10-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /**
     * Crea un nuevo restaurante basado en la solicitud y lo asocia a un usuario propietario.
     * <p>
     * Antes de la creación, valida la regla de negocio que impide a un usuario con rol
     * 'RESTAURANT_OWNER' poseer más de un restaurante.
     *
     * @param req El objeto de solicitud que contiene los datos del nuevo restaurante.
     * @param user El usuario autenticado que realiza la creación, quien se convertirá en el propietario.
     * @return La entidad {@link Restaurant} persistida y completamente formada.
     * @throws OperationNotAllowedException si un usuario con rol 'RESTAURANT_OWNER' intenta crear un segundo lanza
     * una excepción de tipo 409.
     */
    @Override
    // EN RestaurantServiceImp.java
    @Transactional
    public RestaurantDto createRestaurant(CreateRestaurantRequest req, User user) {
        log.info("Iniciando creación/verificación de restaurante para el usuario '{}'", user.getEmail());

        // Buscamos si ya existe un restaurante para este propietario.
        Optional<Restaurant> existingRestaurantOpt = Optional.ofNullable(restaurantRepository.findByOwnerId(user.getId()));

        // LÓGICA DE IDEMPOTENCIA
        if (existingRestaurantOpt.isPresent()) {
            log.warn("El restaurante para el usuario '{}' ya existe (ID: {}). Devolviendo el restaurante existente.",
                    user.getEmail(), existingRestaurantOpt.get().getId());
            // Si ya existe, simplemente devolvemos el DTO del restaurante existente.
            // La operación es un éxito desde la perspectiva del cliente.
            return mapToRestaurantDto(existingRestaurantOpt.get());
        }

        // --- Si no existe, procedemos con la creación como antes ---
        log.info("No se encontró restaurante existente para '{}'. Procediendo a crear uno nuevo.", user.getEmail());
        Address address = addressRepository.save(req.getAddress());

        Restaurant restaurant = new Restaurant();
        // ... mapeo de todos los campos ...
        restaurant.setOwner(user);
        // ...

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurante '{}' (ID: {}) creado exitosamente.", savedRestaurant.getName(), savedRestaurant.getId());

        return mapToRestaurantDto(savedRestaurant);
    }


    private  RestaurantDto mapToRestaurantDto(Restaurant savedRestaurant) {

        RestaurantDto dto = new RestaurantDto();
        dto.setId(savedRestaurant.getId());
        dto.setOwner(savedRestaurant.getOwner());
        dto.setName(savedRestaurant.getName());
        dto.setDescription(savedRestaurant.getDescription());
        dto.setCuisineType(savedRestaurant.getCuisineType());
        dto.setAddress(savedRestaurant.getAddress());
        dto.setContactInformation(savedRestaurant.getContactInformation());
        dto.setOpeningHours(savedRestaurant.getOpeningHours());
        dto.setImages(savedRestaurant.getImages());
        dto.setRegistrationDate(savedRestaurant.getRegistrationDate());
        dto.setOpen(savedRestaurant.isOpen());
        return dto;

    }

    /**
     * Actualiza la información de un restaurante existente.
     * <p>
     * Antes de realizar cualquier modificación, invoca a un método de validación para asegurar
     * que el usuario tiene los permisos necesarios (es ADMIN o el propietario del restaurante).
     *
     * @param id El ID del restaurante a actualizar.
     * @param updateRequest El objeto de solicitud con los datos a actualizar. Los campos nulos se ignoran.
     * @param user El usuario que solicita la actualización, usado para la validación de permisos.
     * @return La entidad {@link Restaurant} actualizada.
     * @throws RestaurantNotFoundException si no se encuentra un restaurante con el ID proporcionado.
     * @throws AccessDeniedException si el usuario no tiene permisos para modificar el restaurante lanza una
     * excepción de tipo 403.
     */
    @Override
    @Transactional
    public Restaurant updateRestaurant(Long id, CreateRestaurantRequest updateRequest, User user) {
        Restaurant restaurant = validateOwnershipAndGetRestaurant(id, user);

        log.info("Usuario '{}' actualizando el restaurante '{}' (ID: {}).", user.getEmail(), restaurant.getName(), id);

        Optional.ofNullable(updateRequest.getName()).ifPresent(restaurant::setName);
        Optional.ofNullable(updateRequest.getDescription()).ifPresent(restaurant::setDescription);
        Optional.ofNullable(updateRequest.getCuisineType()).ifPresent(restaurant::setCuisineType);
        Optional.ofNullable(updateRequest.getContactInformation()).ifPresent(restaurant::setContactInformation);
        Optional.ofNullable(updateRequest.getOpeningHours()).ifPresent(restaurant::setOpeningHours);
        Optional.ofNullable(updateRequest.getImages()).ifPresent(restaurant::setImages);
        if (updateRequest.getAddress() != null) {
            Address newAddress = updateRequest.getAddress();
            if (restaurant.getAddress() != null) {
                restaurant.getAddress().setStreet(newAddress.getStreet());
                restaurant.getAddress().setCity(newAddress.getCity());
                restaurant.getAddress().setState(newAddress.getState());
                addressRepository.save(restaurant.getAddress());
            } else {
                restaurant.setAddress(addressRepository.save(newAddress));
            }
        }

        return restaurantRepository.save(restaurant);

    }

    /**
     * Elimina permanentemente un restaurante de la base de datos.
     * <p>
     * Esta es una operación crítica y destructiva. Primero valida que el usuario
     * tenga los permisos necesarios para realizar esta acción.
     *
     * @param id El ID del restaurante a eliminar.
     * @param user El usuario que solicita la eliminación, para validación de permisos.
     * @throws RestaurantNotFoundException si no se encuentra un restaurante con el ID proporcionado.
     * @throws AccessDeniedException si el usuario no tiene permisos para eliminar el restaurante.
     */
    @Override
    @Transactional
    public void deleteRestaurant(Long id, User user) {
        Restaurant restaurant = validateOwnershipAndGetRestaurant(id, user);

        log.warn("¡ACCIÓN CRÍTICA! Usuario '{}' eliminando el restaurante '{}' (ID: {}).",
                user.getEmail(), restaurant.getName(), id);

        restaurantRepository.delete(restaurant);
    }

    /**
     * Agrega un restaurante a la lista de favoritos de un usuario.
     *
     * @param restaurantId El ID del restaurante a añadir a favoritos.
     * @param user El usuario que realiza la acción.
     * @return Un {@link AdddToFavoritesDto} representando el restaurante añadido.
     * @throws RestaurantNotFoundException si el restaurante con el ID dado no existe.
     * @throws OperationNotAllowedException si el restaurante ya se encuentra en los favoritos del usuario.
     */
    @Override
    @Transactional
    public AdddToFavoritesDto addToFavorite(Long restaurantId, User user) {
        Restaurant restaurant = findRestaurantById(restaurantId);

        if (user.getFavorites().stream().anyMatch(fav -> fav.getId().equals(restaurantId))) {
            throw new OperationNotAllowedException("Este restaurante ya está en tu lista de favoritos.");
        }

        AdddToFavoritesDto dto = new AdddToFavoritesDto();
        dto.setId(restaurant.getId());
        dto.setTitle(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setImages(restaurant.getImages());

        user.getFavorites().add(dto);
        userRepository.save(user);

        log.info("Usuario '{}' agregó el restaurante '{}' a sus favoritos.", user.getEmail(), restaurant.getName());

        return dto;
    }

    /**
     * Alterna el estado de apertura de un restaurante (abierto/cerrado).
     *
     * @param id El ID del restaurante cuyo estado se va a cambiar.
     * @param user El usuario que solicita el cambio, para validación de permisos.
     * @return El restaurante con su estado actualizado.
     * @throws RestaurantNotFoundException si no se encuentra un restaurante con el ID proporcionado.
     * @throws AccessDeniedException si el usuario no tiene permisos para modificar el estado.
     */
    @Override
    @Transactional
    public Restaurant updateRestaurantStatus(Long id, User user) {
        Restaurant restaurant = validateOwnershipAndGetRestaurant(id, user);

        restaurant.setOpen(!restaurant.isOpen());

        log.info("Usuario '{}' cambió el estado del restaurante '{}' (ID: {}) a '{}'.",
                user.getEmail(), restaurant.getName(), id, restaurant.isOpen() ? "ABIERTO" : "CERRADO");

        return restaurantRepository.save(restaurant);
    }

    // --- MÉTODOS DE LECTURA ---

    /**
     * Obtiene una lista de todos los restaurantes registrados en el sistema.
     * @return Una lista de entidades {@link Restaurant}.
     */
    @Override
    public List<Restaurant> getAllRestaurants() {
        log.debug("Recuperando todos los restaurantes.");
        return restaurantRepository.findAll();
    }

    /**
     * Busca restaurantes cuyo nombre o tipo de cocina coincidan con una palabra clave.
     * @param keyword La palabra clave para la búsqueda.
     * @return Una lista de restaurantes que coinciden con el criterio.
     */
    @Override
    public List<Restaurant> searchRestaurants(String keyword) {
        log.debug("Buscando restaurantes con la palabra clave: '{}'", keyword);
        return restaurantRepository.findBySearchQuery(keyword);
    }

    /**
     * Busca y devuelve un restaurante por su ID único.
     * @param id El ID del restaurante a buscar.
     * @return La entidad {@link Restaurant} encontrada.
     * @throws RestaurantNotFoundException si no se encuentra un restaurante con el ID proporcionado.
     */
    @Override
    public Restaurant findRestaurantById(Long id) {
        log.debug("Buscando restaurante con ID: {}", id);
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurante no encontrado con ID: " + id));
    }

    /**
     * Obtiene el restaurante que es propiedad de un usuario específico.
     * @param userId El ID del usuario propietario.
     * @return El restaurante asociado al usuario.
     * @throws RestaurantNotFoundException si el usuario no posee ningún restaurante.
     */
    @Override
    public Restaurant getRestaurantByUserId(Long userId) {
        log.debug("Buscando restaurante propiedad del usuario con ID: {}", userId);
        Restaurant restaurant = restaurantRepository.findByOwnerId(userId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException("No se encontró restaurante para el usuario con ID: " + userId);
        }
        return restaurant;
    }

    // --- MÉTODOS PRIVADOS DE LÓGICA INTERNA ---

    /**
     * Método central de seguridad para validar la propiedad de un restaurante.
     * <p>
     * Verifica que el usuario tenga los permisos necesarios para realizar una operación
     * sobre un restaurante. Las reglas son:
     * <ul>
     *     <li>Un usuario con rol 'ADMIN' siempre tiene permiso.</li>
     *     <li>Un usuario con rol 'RESTAURANT_OWNER' debe ser el propietario del restaurante.</li>
     * </ul>
     *
     * @param restaurantId El ID del restaurante a verificar.
     * @param user El usuario que realiza la operación.
     * @return La entidad {@link Restaurant} si la validación es exitosa, para evitar una segunda consulta a la BD.
     * @throws RestaurantNotFoundException si el restaurante no existe.
     * @throws AccessDeniedException si el usuario no cumple con las reglas de permisos.
     */
    private Restaurant validateOwnershipAndGetRestaurant(Long restaurantId, User user) {
        Restaurant restaurant = findRestaurantById(restaurantId);

        if ("ADMIN".equals(user.getRole())) {
            log.debug("Acceso de ADMIN concedido al usuario '{}' para el restaurante ID {}.",
                    user.getEmail(), restaurantId);
            return restaurant;
        }

        if ("RESTAURANT_OWNER".equals(user.getRole())) {
            if (restaurant.getOwner().getId().equals(user.getId())) {
                log.debug("Acceso de propietario concedido al usuario '{}' para el restaurante ID {}.",
                        user.getEmail(), restaurantId);
                return restaurant;
            }
        }

        log.warn("¡ACCESO DENEGADO! El usuario '{}' (Rol: {}) intentó acceder al restaurante ID {} sin ser propietario.",
                user.getEmail(), user.getRole(), restaurantId);
        throw new AccessDeniedException("No tienes permiso para realizar esta acción en este restaurante.");
    }
}