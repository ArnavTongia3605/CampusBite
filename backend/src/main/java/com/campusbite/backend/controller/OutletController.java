package com.campusbite.backend.controller;

import com.campusbite.backend.entity.FoodItem;
import com.campusbite.backend.repository.FoodItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outlets")
@CrossOrigin("*")
public class OutletController {

    private final FoodItemRepository repo;

    public OutletController(FoodItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<FoodItem> getAllFoodItems() {
        return repo.findAll();
    }
}