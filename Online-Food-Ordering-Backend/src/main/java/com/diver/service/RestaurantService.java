package com.diver.service;

import com.diver.dto.AdddToFavoritesDto;

import com.diver.dto.RestaurantDto;
import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.request.CreateRestaurantRequest;

import java.util.List;


public interface RestaurantService {
/**
         * Crea un nuevo restaurante asociado al usuario proporcionado.
         *
         * @param req  Datos para crear el restaurante.
         * @param user Usuario que realiza la operación.
         * @return Restaurante creado.
         */
        RestaurantDto createRestaurant(CreateRestaurantRequest req, User user);

        /**
         * Actualiza un restaurante existente identificado por su ID, validando el usuario.
         *
         * @param id             ID del restaurante a actualizar.
         * @param updateRequest  Datos actualizados del restaurante.
         * @param user           Usuario que realiza la operación.
         * @return Restaurante actualizado.
         */
        Restaurant updateRestaurant(Long id, CreateRestaurantRequest updateRequest, User user);

        /**
         * Elimina un restaurante por su ID, validando el usuario.
         *
         * @param id   ID del restaurante a eliminar.
         * @param user Usuario que realiza la operación.
         */
        void deleteRestaurant(Long id, User user);

        /**
         * Obtiene la lista de todos los restaurantes.
         *
         * @return Lista de restaurantes.
         */
        List<Restaurant> getAllRestaurants();

        /**
         * Busca restaurantes por palabra clave.
         *
         * @param keyword Palabra clave para buscar.
         * @return Lista de restaurantes que coinciden.
         */
        List<Restaurant> searchRestaurants(String keyword);

        /**
         * Busca un restaurante por su ID.
         *
         * @param id ID del restaurante.
         * @return Restaurante encontrado o null si no existe.
         */
        Restaurant findRestaurantById(Long id);

        /**
         * Obtiene el restaurante asociado a un usuario por su ID.
         *
         * @param userId ID del usuario.
         * @return Restaurante asociado al usuario.
         */
        Restaurant getRestaurantByUserId(Long userId);

        /**
         * Agrega un restaurante a la lista de favoritos del usuario.
         *
         * @param restaurantId ID del restaurante.
         * @param user         Usuario que realiza la operación.
         * @return DTO del restaurante actualizado.
         */
        List<AdddToFavoritesDto> addToFavorite(Long restaurantId, User user);

        /**
         * Actualiza el estado de un restaurante, validando el usuario.
         *
         * @param id   ID del restaurante.
         * @param user Usuario que realiza la operación.
         * @return Restaurante con el estado actualizado.
         */
        RestaurantDto updateRestaurantStatus(Long id, User user);
}