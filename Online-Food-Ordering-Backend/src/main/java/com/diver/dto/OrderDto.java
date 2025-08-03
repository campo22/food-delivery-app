package com.diver.dto;

import com.diver.model.Address;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private UserSimpleDto customer;
    private RestaurantSimpleDto restaurant;
    private Long totalAmount;
    private String orderStatus;
    private LocalDateTime createdAt;
    private Address deliveryAddress;
    private List<OrderItemDto> items;
    private int totalItemCount;

}
