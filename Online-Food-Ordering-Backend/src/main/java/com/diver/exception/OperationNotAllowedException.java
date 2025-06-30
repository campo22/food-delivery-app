package com.diver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando una operación es inválida debido a una regla de negocio,
 * no por falta de permisos. Por ejemplo, un usuario con rol RESTAURANT_OWNER
 * intentando crear un segundo restaurante cuando la regla de negocio solo permite uno.
 *
 * Esta excepción resultará en una respuesta HTTP 400 Bad Request o 409 Conflict.
 * Usamos 409 Conflict ya que la petición es válida, pero entra en conflicto
 * con el estado actual del recurso.
 */
@ResponseStatus(HttpStatus.CONFLICT) // 409 es un buen código para violaciones de reglas de negocio
public class OperationNotAllowedException extends RuntimeException {

    /**
     * Constructor que acepta un mensaje detallando la regla de negocio violada.
     * @param message El mensaje de error.
     */
    public OperationNotAllowedException(String message) {
        super(message);
    }
}