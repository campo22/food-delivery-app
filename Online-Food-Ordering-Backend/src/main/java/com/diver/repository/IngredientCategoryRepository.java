package com.diver.repository;

import com.diver.model.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {


  List<IngredientCategory> findByRestaurantId(Long restaurantId);
}