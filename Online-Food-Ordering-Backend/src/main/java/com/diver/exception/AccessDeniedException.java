package com.diver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un usuario autenticado intenta realizar una acción
 * para la cual no tiene los permisos necesarios (p. ej., un RESTAURANT_OWNER
 * intentando modificar un restaurante que no le pertenece).
 *
 * Esta excepción resultará en una respuesta HTTP 403 Forbidden.
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // Anotación clave para que Spring devuelva un 403
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructor que acepta un mensaje detallando la razón de la denegación.
     * @param message El mensaje de error.
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}