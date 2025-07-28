package com.diver.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * DTO para la solicitud de añadir un ítem al carrito de compras.
 * <p>
 * Este objeto encapsula toda la información que un cliente necesita enviar
 * para agregar un plato de comida a su carrito, incluyendo personalizaciones
 * como los ingredientes.
 */
@Data
public class AddCartItemRequest {

    /**
     * El ID único del plato de comida (Food) que se desea añadir.
     * Es obligatorio.
     */
    @Schema(
            description = "ID del plato de comida a añadir",
            example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del plato no puede ser nulo.")
    private Long foodId;

    /**
     * La cantidad del plato que se desea añadir.
     * Debe ser al menos 1.
     */
    @Schema(
            description = "Cantidad del ítem a añadir",
            example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private int quantity;

    /**
     * Una lista de strings que representan los ingredientes personalizados
     * o las notas para este ítem específico del carrito.
     * Este campo es opcional.
     * <p>
     * Ejemplo: ["Sin cebolla", "Extra de queso", "Poco picante"]
     */
    @Schema(description = "Lista de ingredientes o notas personalizadas para el ítem",
            example = "[\"Sin cebolla\", \"Extra de queso\"]")
    private List<String> ingredients;

    // No se necesita más información. El `userId` o `cartId` se obtienen
    // del usuario autenticado en el backend.
}