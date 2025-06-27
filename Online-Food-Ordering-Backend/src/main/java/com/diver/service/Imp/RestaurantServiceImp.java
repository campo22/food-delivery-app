package com.diver.service.Imp;

import com.diver.dto.RestaurantDto;
import com.diver.model.Address;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.repository.AddressRepository;
import com.diver.repository.RestaurantRepository;
import com.diver.repository.UserRepository;
import com.diver.request.CreateRestaurantRequest;
import com.diver.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la gestión de restaurantes en la aplicación.
 * Proporciona métodos para crear, actualizar, eliminar, buscar y gestionar
 * el estado de los restaurantes, así como agregar favoritos para usuarios.
 */
@Service
public class RestaurantServiceImp implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository; // Repositorio para operaciones CRUD de restaurantes

    @Autowired
    private AddressRepository addressRepository; // Repositorio para operaciones CRUD de direcciones

    @Autowired
    private UserRepository userRepository; // Repositorio para operaciones CRUD de usuarios

    /**
     * Crea un nuevo restaurante con la información proporcionada y lo asocia a un usuario propietario.
     * Guarda primero la dirección y luego el restaurante con la fecha de registro actual.
     *
     * @param req  Objeto que contiene los datos del restaurante (nombre, descripción, etc.) y su dirección.
     * @param user El usuario que será el propietario del restaurante.
     * @return El restaurante creado y persistido en la base de datos.
     * @throws IllegalArgumentException Si el nombre o la dirección son nulos o no válidos.
     */
    @Override
    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {
        if (req.getName() == null || req.getAddress() == null) {
            throw new IllegalArgumentException("Nombre y dirección son campos obligatorios");
        }
        Address address = addressRepository.save(req.getAddress());
        Restaurant restaurant = new Restaurant();
        restaurant.setAddress(address);
        restaurant.setContactInformation(req.getContactInformation());
        restaurant.setCuisineType(req.getCuisineType());
        restaurant.setDescription(req.getDescription());
        restaurant.setName(req.getName());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setOwner(user);
        restaurant.setImages(req.getImages());
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setOpen(true);
        return restaurantRepository.save(restaurant);
    }

    /**
     * Actualiza la información de un restaurante existente, incluyendo su dirección si se proporciona.
     * Solo actualiza los campos que han cambiado, asegurando consistencia transaccional.
     *
     * @param id            El ID del restaurante a actualizar.
     * @param updateRequest Objeto con los datos actualizados del restaurante.
     * @return El restaurante actualizado y persistido.
     * @throws RestaurantNotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Override
    @Transactional
    public Restaurant updateRestaurant(Long id, CreateRestaurantRequest updateRequest) throws RestaurantNotFoundException {
        Restaurant restaurant = findRestaurantById(id);
        if (restaurant == null) {
            throw new RestaurantNotFoundException("Restaurante no encontrado con ID: " + id);
        }
        if (updateRequest.getCuisineType() != null && !updateRequest.getCuisineType().equals(restaurant.getCuisineType())) {
            restaurant.setCuisineType(updateRequest.getCuisineType());
        }
        if (updateRequest.getDescription() != null && !updateRequest.getDescription().equals(restaurant.getDescription())) {
            restaurant.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getName() != null && !updateRequest.getName().equals(restaurant.getName())) {
            restaurant.setName(updateRequest.getName());
        }
        if (updateRequest.getContactInformation() != null && !updateRequest.getContactInformation().equals(restaurant.getContactInformation())) {
            restaurant.setContactInformation(updateRequest.getContactInformation());
        }
        if (updateRequest.getImages() != null && !updateRequest.getImages().equals(restaurant.getImages())) {
            restaurant.setImages(updateRequest.getImages());
        }
        if (updateRequest.getOpeningHours() != null && !updateRequest.getOpeningHours().equals(restaurant.getOpeningHours())) {
            restaurant.setOpeningHours(updateRequest.getOpeningHours());
        }
        if (updateRequest.getAddress() != null) {
            Address newAddress = updateRequest.getAddress();
            if (newAddress.getStreet() == null || newAddress.getCity() == null || newAddress.getState() == null) {
                throw new IllegalArgumentException("La dirección debe contener calle, ciudad y estado.");
            }
            if (restaurant.getAddress() != null) {
                Address existing = restaurant.getAddress();
                if (newAddress.getStreet() != null) existing.setStreet(newAddress.getStreet());
                if (newAddress.getCity() != null) existing.setCity(newAddress.getCity());
                if (newAddress.getState() != null) existing.setState(newAddress.getState());
                addressRepository.save(existing);
            } else {
                addressRepository.save(newAddress);
                restaurant.setAddress(newAddress);
            }
        }
        return restaurantRepository.save(restaurant);
    }

    /**
     * Elimina un restaurante de la base de datos por su ID, incluyendo su dirección asociada.
     * Asegura que las operaciones sean atómicas para mantener la integridad de los datos.
     *
     * @param id El ID del restaurante a eliminar.
     * @throws RestaurantNotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Override
    @Transactional
    public void deleteRestaurant(Long id) throws RestaurantNotFoundException {
        Restaurant restaurant = findRestaurantById(id);
        if (restaurant == null) {
            throw new RestaurantNotFoundException("Restaurante no encontrado con ID: " + id);
        }
        if (restaurant.getAddress() != null) {
            addressRepository.delete(restaurant.getAddress());
        }
        restaurantRepository.delete(restaurant);
    }

    /**
     * Obtiene la lista de todos los restaurantes registrados en la base de datos.
     * Realiza una operación de lectura sin modificar datos.
     *
     * @return Lista de todos los restaurantes.
     */
    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /**
     * Busca restaurantes cuyos nombres o tipos de cocina contengan la palabra clave proporcionada.
     * Utiliza una consulta personalizada del repositorio para una búsqueda flexible.
     *
     * @param keyword Palabra clave para buscar (e.g., "pizza", "italiana").
     * @return Lista de restaurantes que coinciden con el criterio de búsqueda.
     */
    @Override
    public List<Restaurant> searchRestaurants(String keyword) {
        return restaurantRepository.findBySearchQuery(keyword);
    }

    /**
     * Busca un restaurante por su ID en la base de datos.
     * Realiza una operación de lectura sin modificar datos.
     *
     * @param id ID del restaurante a buscar.
     * @return Restaurante encontrado.
     * @throws RestaurantNotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Override
    public Restaurant findRestaurantById(Long id) throws RestaurantNotFoundException {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isEmpty()) {
            throw new RestaurantNotFoundException("Restaurante no encontrado con ID: " + id);
        }
        return optionalRestaurant.get();
    }

    /**
     * Obtiene el restaurante asociado a un usuario por su ID.
     * Realiza una operación de lectura sin modificar datos.
     *
     * @param userId ID del usuario propietario del restaurante.
     * @return Restaurante asociado al usuario.
     * @throws RestaurantNotFoundException Si no se encuentra un restaurante para el usuario.
     */
    @Override
    public Restaurant getRestaurantByUserId(Long userId) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.findByOwnerId(userId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException("Restaurante no encontrado para el usuario con ID: " + userId);
        }
        return restaurant;
    }

    /**
     * Agrega un restaurante a la lista de favoritos de un usuario.
     * Actualiza la entidad del usuario en la base de datos y asegura consistencia transaccional.
     *
     * @param restaurantId ID del restaurante a favoritar.
     * @param user         Usuario que agrega el favorito.
     * @return DTO del restaurante agregado a favoritos.
     * @throws RestaurantNotFoundException Si no se encuentra el restaurante o ya está en favoritos.
     */
    @Override
    @Transactional
    public RestaurantDto addToFavorite(Long restaurantId, User user) throws RestaurantNotFoundException {
        Restaurant restaurant = findRestaurantById(restaurantId);
        RestaurantDto dto = new RestaurantDto();
        dto.setId(restaurant.getId());
        dto.setTitle(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setImages(restaurant.getImages());
        if (user.getFavorites() != null && user.getFavorites().contains(dto)) {
            throw new RestaurantNotFoundException("El restaurante ya está en favoritos");
        } else {
            if (user.getFavorites() == null) {
                user.setFavorites(new ArrayList<>());
            }
            user.getFavorites().add(dto);
            userRepository.save(user);
        }
        return dto;
    }

    /**
     * Actualiza el estado de un restaurante (abierto/cerrado) en la base de datos.
     * Alterna el valor de 'open' y asegura que el cambio sea persistente.
     *
     * @param id ID del restaurante.
     * @return Restaurante con el estado actualizado.
     * @throws RestaurantNotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Override
    @Transactional
    public Restaurant updateRestaurantStatus(Long id) throws RestaurantNotFoundException {
        Restaurant restaurant = findRestaurantById(id);
        restaurant.setOpen(!restaurant.isOpen());
        return restaurantRepository.save(restaurant);
    }

    /**
     * Excepción personalizada para cuando un restaurante no se encuentra.
     */
    public static class RestaurantNotFoundException extends Exception {
        public RestaurantNotFoundException(String message) {
            super(message);
        }
    }
}