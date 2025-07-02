package com.diver.controller;

import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    final RestaurantService restaurantService;

    public ResponseEntity<Restaurant> searchRestaurants(

            @AuthenticationPrincipal User user
    ) {
return null;
    }



}
