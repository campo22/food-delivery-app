package com.diver.service.Imp;

import com.diver.dto.RestaurantDto;
import com.diver.model.Address;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.repository.AddressRepository;
import com.diver.repository.RestaurantRepository;
import com.diver.request.CreateRestaurantRequest;
import com.diver.service.RestaurantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestaurantSeriviceImp implements RestaurantService {


    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private  UserServiceImp userServiceImp;


    /**
     * Crea un nuevo restaurante con la información proporcionada.
     *
     * @param req  Datos para crear el restaurante.
     * @param user Usuario que crea el restaurante.
     * @return Restaurante creado.
     */
    @Override
    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {

        if(req.getName()== null || req.getAddress()== null){
            throw new IllegalArgumentException("nombre y direccion son campos obligatorios");
        }

        Address address= null;
        if (req.getAddress() != null) {
            address= addressRepository.save(req.getAddress());
        }

        Restaurant restaurant= new Restaurant();
        restaurant.setAddress(address);
        restaurant.setContactInformation(req.getContactInformation());
        restaurant.setCuisineType(req.getCuisineType());
        restaurant.setDescription(req.getDescription());
        restaurant.setName(req.getName());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setOwner(user);
        restaurant.setImages(req.getImages());
        restaurant.setRegistrationDate(LocalDateTime.now());

        return restaurantRepository.save(restaurant);
    }

    /**
     * Actualiza la información de un restaurante existente.
     *
     * @param id            ID del restaurante a actualizar.
     * @param updateRequest Datos actualizados del restaurante.
     * @return Restaurante actualizado.
     * @throws Exception si ocurre un error durante la actualización.
     */
    @Override
    @Transactional
    public Restaurant updateRestaurant(Long id, CreateRestaurantRequest updateRequest) throws Exception {

        Restaurant restaurant= findRestaurantById(id);


        // Si el tipo de cocina proporcionado en la solicitud de actualización no es nulo
        // y es diferente al tipo de cocina actual del restaurante, actualiza el tipo de cocina.
        if (updateRequest.getCuisineType() != null && !updateRequest.getCuisineType()
                .equals(restaurant.getCuisineType())) {
            restaurant.setCuisineType(updateRequest.getCuisineType());
        }
        if (updateRequest.getDescription() != null && !updateRequest.getDescription()
                .equals(restaurant.getDescription())) {
            restaurant.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getName() != null && !updateRequest.getName().equals(restaurant.getName())) {
            restaurant.setName(updateRequest.getName());
        }
        if (updateRequest.getContactInformation() != null && !updateRequest.getContactInformation()
                .equals(restaurant.getContactInformation())) {
            restaurant.setContactInformation(updateRequest.getContactInformation());
        }
        if (updateRequest.getImages() != null && !updateRequest.getImages().equals(restaurant.getImages())) {
            restaurant.setImages(updateRequest.getImages());
        }
        if (updateRequest.getOpeningHours() != null && !updateRequest.getOpeningHours()
                .equals(restaurant.getOpeningHours())) {
            restaurant.setOpeningHours(updateRequest.getOpeningHours());
        }
        // actualizar la direccion
        if (updateRequest.getAddress() != null) {
            Address newAddress = updateRequest.getAddress();

            if (newAddress.getStreet() == null || newAddress.getCity() == null || newAddress.getState() == null) {
                throw new IllegalArgumentException("La dirección debe contener calle, ciudad y estado.");
            }
            if (restaurant.getAddress() !=null) {

                Address exiting = restaurant.getAddress();
                if (newAddress.getStreet() !=null) exiting.setStreet(newAddress.getStreet());
                if (newAddress.getCity() !=null) exiting.setCity(newAddress.getCity());
                if (newAddress.getState() !=null) exiting.setState(newAddress.getState());

                addressRepository.save(exiting);
            } else {
                addressRepository.save(newAddress);
                restaurant.setAddress(newAddress);

            }

        }

        return restaurantRepository.save(restaurant);
    }

    /**
     * Elimina un restaurante por su ID.
     *
     * @param id ID del restaurante a eliminar.
     * @throws Exception si ocurre un error durante la eliminación.
     */
    @Override
    public void deleteRestaurant(Long id) throws Exception {

    }

    /**
     * Obtiene la lista de todos los restaurantes.
     *
     * @return Lista de restaurantes.
     */
    @Override
    public List<Restaurant> getAllRestaurants() {
        return List.of();
    }

    /**
     * Busca restaurantes según criterios definidos.
     *
     * @return Lista de restaurantes encontrados.
     */
    @Override
    public List<Restaurant> searchRestaurants() {
        return List.of();
    }

    /**
     * Busca un restaurante por su ID.
     *
     * @param id ID del restaurante.
     * @return Restaurante encontrado.
     * @throws Exception si no se encuentra el restaurante.
     */
    @Override
    public Restaurant findRestaurantById(Long id) throws Exception {
        return null;
    }

    /**
     * Obtiene el restaurante asociado a un usuario.
     *
     * @param userId ID del usuario.
     * @return Restaurante del usuario.
     * @throws Exception si no se encuentra el restaurante.
     */
    @Override
    public Restaurant getRestaurantByUserId(Long userId) throws Exception {
        return null;
    }

    /**
     * Agrega un restaurante a la lista de favoritos de un usuario.
     *
     * @param restaurantId ID del restaurante.
     * @param user         Usuario que agrega el favorito.
     * @return DTO del restaurante agregado a favoritos.
     * @throws Exception si ocurre un error.
     */
    @Override
    public RestaurantDto addToFavorite(Long restaurantId, User user) throws Exception {
        return null;
    }

    /**
     * Actualiza el estado de un restaurante.
     *
     * @param id ID del restaurante.
     * @return Restaurante con el estado actualizado.
     * @throws Exception si ocurre un error.
     */
    @Override
    public Restaurant updateRestaurantStatus(Long id) throws Exception {
        return null;
    }

    // metodos auxiliares
    public class RestaurantNotFoundException extends Exception {
        public RestaurantNotFoundException(String message) {
            super(message);
        }
    }
}
