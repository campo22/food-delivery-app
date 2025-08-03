package com.diver.request;

import com.diver.model.Address;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull
    private Long restaurantId;
    @NotNull
    private Address deliveryAddress;



}
