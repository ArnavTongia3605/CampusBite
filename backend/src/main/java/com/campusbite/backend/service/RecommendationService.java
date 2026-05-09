package com.campusbite.backend.service;

import com.campusbite.backend.dto.Dtos.RecommendationItem;
import com.campusbite.backend.dto.Dtos.RecommendationRequest;
import com.campusbite.backend.entity.FoodItem;
import com.campusbite.backend.repository.FoodItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core recommendation logic.
 * Filters food items from DB → sends to Gemini AI → parses and returns results.
 */
@Service
public class RecommendationService {

    private final FoodItemRepository foodItemRepository;
    private final GeminiService      geminiService;
    private final ObjectMapper       objectMapper;

    public RecommendationService(FoodItemRepository foodItemRepository,
                                 GeminiService geminiService) {
        this.foodItemRepository = foodItemRepository;
        this.geminiService      = geminiService;
        this.objectMapper       = new ObjectMapper();
    }

    /**
     * Main recommendation flow:
     * 1. Filter food items from DB
     * 2. If no results with mood filter, try relaxed filter
     * 3. Send filtered items to Gemini AI
     * 4. Parse and return recommendations
     */
    public List<RecommendationItem> recommend(RecommendationRequest request) {
        // Step 1: Filter items from DB
        List<FoodItem> filtered = foodItemRepository.filterItems(
                request.getBudget(),
                request.getMood(),
                request.getFoodType(),
                request.getIsVeg()
        );

        // Step 2: Relaxed filter if no results
        if (filtered.isEmpty()) {
            filtered = foodItemRepository.filterItemsRelaxed(
                    request.getBudget(),
                    request.getFoodType(),
                    request.getIsVeg()
            );
        }

        // Step 3: No items at all — return empty
        if (filtered.isEmpty()) {
            return new ArrayList<>();
        }

        // Step 4: Call Gemini AI
        String geminiResponse = geminiService.getRecommendations(
                filtered,
                request.getBudget(),
                request.getMood(),
                request.getFoodType(),
                request.getIsVeg()
        );

        // Step 5: Parse Gemini response
        if (geminiResponse != null && !geminiResponse.isBlank()) {
            List<RecommendationItem> aiResults = parseGeminiResponse(geminiResponse);
            if (aiResults != null && !aiResults.isEmpty()) {
                return aiResults;
            }
        }

        // Step 6: Fallback — return top 5 filtered items with generic reason
        return filtered.stream()
                .limit(5)
                .map(f -> new RecommendationItem(
                        f.getFoodName(),
                        f.getOutletName(),
                        f.getPrice(),
                        Boolean.TRUE.equals(f.getIsVeg()),
                        "Fits your budget of ₹" + request.getBudget() +
                                " and matches your " + request.getMood() + " craving."
                ))
                .collect(Collectors.toList());
    }

    /**
     * Parses the JSON array returned by Gemini into RecommendationItem objects.
     */
    private List<RecommendationItem> parseGeminiResponse(String text) {
        try {
            // Clean up potential markdown code fences
            String cleaned = text.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("```[a-z]*\\n?", "").replace("```", "").trim();
            }

            // Find the JSON array
            int start = cleaned.indexOf('[');
            int end   = cleaned.lastIndexOf(']');
            if (start == -1 || end == -1) return null;

            cleaned = cleaned.substring(start, end + 1);

            List<GeminiItemDto> rawItems = objectMapper.readValue(
                    cleaned,
                    new TypeReference<List<GeminiItemDto>>() {}
            );

            return rawItems.stream()
                    .map(r -> new RecommendationItem(
                            r.foodName, r.outletName, r.price, r.isVeg, r.reason
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("[RecommendationService] Failed to parse Gemini JSON: " + e.getMessage());
            return null;
        }
    }

    /** Internal DTO for deserializing Gemini JSON response */
    private static class GeminiItemDto {
        public String  foodName;
        public String  outletName;
        public double  price;
        public boolean isVeg;
        public String  reason;
    }
}
