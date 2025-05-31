package com.diver.response;


import com.diver.model.USER_ROLE;
import lombok.Data;

/**
 * Clase que representa la respuesta de autenticación del sistema.
 * Contiene el token JWT, un mensaje descriptivo y el rol del usuario autenticado.
 */
@Data
public class AuthResponse {
    /**
     * Token JWT generado tras una autenticación exitosa.
     */
    private String jwt;

    /**
     * Mensaje descriptivo relacionado con el resultado de la autenticación.
     */
    private String message;

    /**
     * Rol del usuario que ha iniciado sesión en el sistema.
     * @see USER_ROLE
     */
    private USER_ROLE role;
}
