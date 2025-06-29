package com.diver.controller;


import com.diver.model.Restaurant;
import com.diver.model.User;
import com.diver.request.CreateRestaurantRequest;
import com.diver.response.MessageResponse;
import com.diver.service.RestaurantService;
import com.diver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurant")
class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(
            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwt(jwt);

        Restaurant restaurant= restaurantService.createRestaurant(req, user);

        return new ResponseEntity<>( restaurant, HttpStatus.CREATED);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @RequestBody CreateRestaurantRequest req,
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt

    ) throws Exception {
        User user= userService.findUserByJwt(jwt);
        Restaurant restaurant= restaurantService.updateRestaurant(id, req);
        return new ResponseEntity<>(restaurant, HttpStatus.OK );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRestaurant(

            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user= userService.findUserByJwt(jwt);
        restaurantService.deleteRestaurant(id);

        MessageResponse res=new MessageResponse();
        res.setMessage("Restaurante eliminado");
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Restaurant> updateRestaurantStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user= userService.findUserByJwt(jwt);
        Restaurant restaurant= restaurantService.updateRestaurantStatus(id);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Restaurant> getRestaurantByUserId(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user= userService.findUserByJwt(jwt);
        Restaurant restaurant= restaurantService.getRestaurantByUserId(user.getId());
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }



}
