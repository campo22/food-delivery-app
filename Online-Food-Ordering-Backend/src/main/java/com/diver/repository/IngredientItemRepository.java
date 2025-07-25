package com.diver.repository;

import com.diver.model.IngredientItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface IngredientItemRepository extends JpaRepository<IngredientItem, Long> {

    List<IngredientItem> findByRestaurantId(Long restaurantId);
}