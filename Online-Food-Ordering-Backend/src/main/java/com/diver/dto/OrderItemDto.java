package com.diver.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private FoodDto food;
    private int quantity;
    private Long totalPrice;
}
