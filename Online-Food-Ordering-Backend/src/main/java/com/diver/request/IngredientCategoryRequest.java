package com.diver.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
;
;


@Data
public class IngredientCategoryRequest {

    @NotBlank
    private String name;

    @NotNull
    private Long restaurantId;

}
