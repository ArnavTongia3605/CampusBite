package com.campusbite.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outlet_name", nullable = false)
    private String outletName;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(nullable = false)
    private Double price;

    @Column(name = "mood_tag")
    private String moodTag;

    @Column(name = "food_type")
    private String foodType;

    @Column(name = "is_veg")
    private Boolean isVeg;

    @Column(name = "category")
    private String category;

    // Constructors
    public FoodItem() {}

    public FoodItem(String outletName,
                    String foodName,
                    Double price,
                    String moodTag,
                    String foodType,
                    Boolean isVeg,
                    String category) {

        this.outletName = outletName;
        this.foodName = foodName;
        this.price = price;
        this.moodTag = moodTag;
        this.foodType = foodType;
        this.isVeg = isVeg;
        this.category = category;
    }

    public FoodItem(String theGrubHub, String vegThali, double v, String comfort, String lunch, boolean b) {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMoodTag() {
        return moodTag;
    }

    public void setMoodTag(String moodTag) {
        this.moodTag = moodTag;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public Boolean getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(Boolean isVeg) {
        isVeg = isVeg;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}