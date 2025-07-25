package com.diver.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {

    private Long id;
    private UserSimpleDto customer;
    private List<CartItemDto> items;
    private Long total;
}
