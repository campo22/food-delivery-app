package com.diver.service;

import com.diver.dto.UserProfileDto;
import com.diver.model.User;

/**
 * Interfaz que define las operaciones de negocio para la entidad User.
 * <p>
 * Abstrae la implementación de la lógica de negocio, permitiendo un diseño desacoplado
 * y facilitando las pruebas y el mantenimiento.
 */
public interface UserService {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * Este método es utilizado internamente por otros servicios o componentes
     * que necesitan la entidad User completa.
     *
     * @param email El email del usuario a buscar.
     * @return El objeto {@link User} completo.
     * @throws package com.diver.exception;

     /**
      * Excepción lanzada cuando no se encuentra un usuario.
      */
    User findUserByEmail(String email);

    /**
     * Obtiene los datos del perfil de un usuario en un formato seguro para la API.
     * <p>
     * Este método se encarga de convertir la entidad {@link User} en un {@link UserProfileDto},
     * asegurando que solo se expongan los datos necesarios y resolviendo problemas
     * de carga perezosa (Lazy Initialization).
     *
     * @param email El email del usuario cuyo perfil se desea obtener.
     * @return un DTO {@link UserProfileDto} con la información del perfil.
     * @throws com.diver.exception.UserNotFoundException si el usuario no se encuentra.
     */
    UserProfileDto getUserProfileByEmail(String email);

    User findUserById(Long id);

}