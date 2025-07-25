package com.diver.dto;

import lombok.Data;

@Data
public class IngredientItemDto {

    private Long id;
    private String name;
    private boolean inStock;
    private IngredientCategoryDto category;
}
