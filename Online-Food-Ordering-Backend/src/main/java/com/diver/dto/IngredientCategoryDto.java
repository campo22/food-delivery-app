package com.diver.dto;

import lombok.Data;

@Data
public class IngredientCategoryDto {

    private Long id;
    private String name;
    private Long restaurantId;
}
