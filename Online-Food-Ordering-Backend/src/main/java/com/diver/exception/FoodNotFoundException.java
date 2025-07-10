package com.diver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta acceder o modificar un plato de comida (Food)
 * que no existe en la base de datos.
 * <p>
 * Esta excepción es de tipo runtime y está anotada para que Spring Boot
 * la traduzca automáticamente a una respuesta HTTP 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FoodNotFoundException extends RuntimeException {

    /**
     * Constructor que acepta un mensaje detallando la razón del error.
     * @param message El mensaje de error, por ejemplo: "Plato no encontrado con ID: 123".
     */
    public FoodNotFoundException(String message) {
        super(message);
    }
}

