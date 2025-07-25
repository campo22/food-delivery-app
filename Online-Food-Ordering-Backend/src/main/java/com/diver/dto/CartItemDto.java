package com.diver.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartItemDto {
    private Long id;
    private FoodDto food;
    private Integer quantity;
    private List<String> ingredients;
    private Long totalPrice;

}
