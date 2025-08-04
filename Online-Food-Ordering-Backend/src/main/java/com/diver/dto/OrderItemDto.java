package com.diver.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderItemDto {
    private Long id;
    private SimpleFoodDto food;
    private int quantity;
    private Long totalPrice;
    private List<String> ingredients;
}
