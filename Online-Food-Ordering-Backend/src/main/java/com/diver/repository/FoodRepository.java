package com.diver.repository;


import com.diver.dto.FoodDto;
import com.diver.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(Long restaurantId);

    @Query("SELECT f FROM Food f\n" +
            "WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))\n" +
            "   OR LOWER(f.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Food> searchFood(@Param("keyword") String keyword);
}