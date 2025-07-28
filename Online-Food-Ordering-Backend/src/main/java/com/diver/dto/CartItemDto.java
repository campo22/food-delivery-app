package com.diver.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartItemDto {
    private Long id;
    private Integer quantity;
    private List<String> ingredients;
    private Long totalPrice;
    private SimpleFoodDto food;

    // Clase estática anidada
    @Data
    public static class SimpleFoodDto {
        private Long id;
        private String name;
        private String image;
    }
}
