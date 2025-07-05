package com.diver.dto;

import com.diver.model.Address;
import com.diver.model.ContactInformation;
import com.diver.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RestaurantDto {
    private  Long id;
    private  User owner;
    private  String name;
    private  String description;
    private  String cuisineType;
    private  Address address;
    private  ContactInformation contactInformation;
    private  String openingHours;
    private  List<String> images;
    private  LocalDateTime registrationDate;
    private  boolean open;

}


