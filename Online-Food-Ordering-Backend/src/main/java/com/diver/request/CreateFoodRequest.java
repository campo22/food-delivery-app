package com.diver.request;

import com.diver.model.Category;
import com.diver.model.IngredientItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class CreateFoodRequest {

    private String name;
    private String description;
    private Long price;

    private List<String> images;
    private boolean isVegetarian;
    private boolean isSeasonal; // signa que el plato es temporada
    private boolean isAvailable;

    @NonNull
    private Long categoryId;


    private Long restaurantId;
    @Schema(
            description = "Lista de IDs de los ingredientes a asociar con el plato",
            example = "[10, 25, 32]"
    )
    private List<Long> ingredientsIds;

}
