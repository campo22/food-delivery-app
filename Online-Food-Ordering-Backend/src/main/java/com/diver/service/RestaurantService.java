package com.diver.service;

import com.diver.dto.RestaurantDto;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.request.CreateRestaurantRequest;

import java.util.List;

public interface RestaurantService {

    /**
         * Crea un nuevo restaurante con la información proporcionada.
         * @param req Datos para crear el restaurante.
         * @param user Usuario que crea el restaurante.
         * @return Restaurante creado.
         */
        public Restaurant createRestaurant(CreateRestaurantRequest req, User user);

        /**
         * Actualiza la información de un restaurante existente.
         * @param id ID del restaurante a actualizar.
         * @param updateRequest Datos actualizados del restaurante.
         * @return Restaurante actualizado.
         * @throws Exception si ocurre un error durante la actualización.
         */
        public Restaurant updateRestaurant(Long id, CreateRestaurantRequest updateRequest) throws Exception ;

        /**
         * Elimina un restaurante por su ID.
         * @param id ID del restaurante a eliminar.
         * @throws Exception si ocurre un error durante la eliminación.
         */
        public void deleteRestaurant(Long id) throws Exception;

        /**
         * Obtiene la lista de todos los restaurantes.
         * @return Lista de restaurantes.
         */
        public List <Restaurant> getAllRestaurants();

        /**
         * Busca restaurantes según criterios definidos.
         * @return Lista de restaurantes encontrados.
         */
        public List <Restaurant> searchRestaurants();

        /**
         * Busca un restaurante por su ID.
         * @param id ID del restaurante.
         * @return Restaurante encontrado.
         * @throws Exception si no se encuentra el restaurante.
         */
        public Restaurant findRestaurantById(Long id) throws Exception;

        /**
         * Obtiene el restaurante asociado a un usuario.
         * @param userId ID del usuario.
         * @return Restaurante del usuario.
         * @throws Exception si no se encuentra el restaurante.
         */
        public Restaurant getRestaurantByUserId(Long userId) throws Exception;

        /**
         * Agrega un restaurante a la lista de favoritos de un usuario.
         * @param restaurantId ID del restaurante.
         * @param user Usuario que agrega el favorito.
         * @return DTO del restaurante agregado a favoritos.
         * @throws Exception si ocurre un error.
         */
        public RestaurantDto addToFavorite(Long restaurantId, User user) throws Exception;

        /**
         * Actualiza el estado de un restaurante.
         * @param id ID del restaurante.
         * @return Restaurante con el estado actualizado.
         * @throws Exception si ocurre un error.
         */
        public Restaurant updateRestaurantStatus(Long id) throws Exception;

}
