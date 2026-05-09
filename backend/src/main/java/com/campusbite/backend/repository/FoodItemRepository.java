package com.campusbite.backend.repository;

import com.campusbite.backend.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for FoodItem entity.
 * Contains custom JPQL queries for filtering food items.
 */
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    /**
     * Filter food items by budget, mood, food type and veg preference.
     * isVeg parameter is optional (null means both veg and non-veg).
     */
    @Query("SELECT f FROM FoodItem f WHERE " +
            "f.price <= :budget AND " +
            "LOWER(f.moodTag) = LOWER(:mood) AND " +
            "LOWER(f.foodType) = LOWER(:foodType) AND " +
            "(:isVeg IS NULL OR f.isVeg = :isVeg)")
    List<FoodItem> filterItems(@Param("budget")   double budget,
                               @Param("mood")     String mood,
                               @Param("foodType") String foodType,
                               @Param("isVeg")    Boolean isVeg);

    /**
     * Filter with relaxed constraints (ignores mood) for fallback.
     */
    @Query("SELECT f FROM FoodItem f WHERE " +
            "f.price <= :budget AND " +
            "LOWER(f.foodType) = LOWER(:foodType) AND " +
            "(:isVeg IS NULL OR f.isVeg = :isVeg)")
    List<FoodItem> filterItemsRelaxed(@Param("budget")   double budget,
                                      @Param("foodType") String foodType,
                                      @Param("isVeg")    Boolean isVeg);

    /**
     * Get all distinct outlet names.
     */
    @Query("SELECT DISTINCT f.outletName FROM FoodItem f")
    List<String> findDistinctOutletNames();

    List<FoodItem> findByOutletName(String outletName);
}
