package com.campusbite.backend.controller;

import com.campusbite.backend.dto.Dtos.RecommendationItem;
import com.campusbite.backend.dto.Dtos.RecommendationRequest;
import com.campusbite.backend.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for AI-powered food recommendations.
 * This endpoint is protected — requires valid JWT token.
 *
 * POST /api/recommendations → Returns AI recommendations
 */
@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Accepts user preferences and returns AI-powered food recommendations.
     *
     * Request body:
     * {
     *   "budget": 100,
     *   "mood": "spicy",
     *   "foodType": "lunch",
     *   "isVeg": true   // or false, or null for both
     * }
     */
    @PostMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@RequestBody RecommendationRequest request) {
        // Validate request
        if (request.getBudget() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Budget must be greater than 0."));
        }
        if (request.getMood() == null || request.getMood().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mood is required."));
        }
        if (request.getFoodType() == null || request.getFoodType().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Food type is required."));
        }

        try {
            List<RecommendationItem> recommendations = recommendationService.recommend(request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", recommendations.size(),
                    "recommendations", recommendations
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to generate recommendations: " + e.getMessage()
                    ));
        }
    }
}
