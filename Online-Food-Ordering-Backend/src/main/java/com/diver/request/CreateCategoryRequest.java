package com.diver.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para la solicitud de creación de una nueva categoría.
 * <p>
 * Este objeto encapsula los datos que el cliente debe proporcionar
 * para crear una categoría, junto con las validaciones correspondientes.
 */
@Data
public class CreateCategoryRequest {

    /**
     * El nombre de la nueva categoría.
     * Este campo es obligatorio y debe tener una longitud adecuada.
     */
    @Schema(description = "Nombre de la nueva categoría", example = "Pizzas Artesanales", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre de la categoría no puede estar vacío.")
    @Size(min = 3, max = 50, message = "El nombre de la categoría debe tener entre 3 y 50 caracteres.")
    private String name;

}