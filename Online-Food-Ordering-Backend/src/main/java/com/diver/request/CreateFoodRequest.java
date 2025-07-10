package com.diver.request;

import com.diver.model.Category;
import com.diver.model.IngredientItem;
import lombok.Data;

import java.util.List;

@Data
public class CreateFoodRequest {

    private String name;
    private String description;
    private Long price;

    private List<String> image;
    private boolean isVegetarian;
    private boolean isSeasonal; // signa que el plato es temporada
    private boolean isAvailable;

    private Category categoryId;
    private Long restaurantId;
    private List<IngredientItem> ingredients;

}
