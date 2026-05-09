package com.campusbite.backend.service;

import com.campusbite.backend.entity.FoodItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service that communicates with Google Gemini API.
 * Sends filtered food items and user preferences to Gemini,
 * and parses the AI-generated recommendations.
 */
@Service
public class GeminiService {

    @Value("${app.gemini.api-key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Builds a structured prompt and calls Gemini API.
     *
     * @param filteredItems List of food items that matched the filters
     * @param budget        User's budget in INR
     * @param mood          User's mood/craving
     * @param foodType      Meal type (breakfast/lunch/snacks/dinner)
     * @param isVeg         Veg preference (null = both)
     * @return JSON string with recommendations array, or null on failure
     */
    public String getRecommendations(List<FoodItem> filteredItems,
                                     int budget, String mood,
                                     String foodType, Boolean isVeg) {
        // Build the items list for the prompt
        StringBuilder itemsList = new StringBuilder();
        for (int i = 0; i < filteredItems.size(); i++) {
            FoodItem item = filteredItems.get(i);
            itemsList.append(String.format("%d. %s from %s — ₹%.0f (%s, %s)%n",
                    i + 1,
                    item.getFoodName(),
                    item.getOutletName(),
                    item.getPrice(),
                    item.getIsVeg() ? "Veg" : "Non-Veg",
                    item.getMoodTag()
            ));
        }

        String vegPref = isVeg == null ? "both veg and non-veg"
                : (isVeg ? "vegetarian only" : "non-vegetarian only");

        // Structured Gemini prompt
        String prompt = String.format("""
                You are a campus food recommendation assistant. A college student needs food suggestions.
                
                Student Preferences:
                - Budget: ₹%d
                - Mood/Craving: %s
                - Meal Type: %s
                - Dietary Preference: %s
                
                Available food items that match the budget and filters:
                %s
                
                Please recommend the TOP 3-5 best matching food items from the list above.
                Return ONLY a valid JSON array with no markdown, no explanation, no preamble.
                Each item in the array must have these exact keys:
                {
                  "foodName": "exact food name from the list",
                  "outletName": "exact outlet name from the list",
                  "price": price as number,
                  "isVeg": true or false,
                  "reason": "1-2 sentence explanation of why this matches the student's preferences"
                }
                """,
                budget, mood, foodType, vegPref, itemsList.toString());

        try {
            // Build Gemini API request body
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.4,
                            "maxOutputTokens", 1024
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = GEMINI_URL + "?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractTextFromGeminiResponse(response.getBody());
            }
        } catch (Exception e) {
            System.err.println("[GeminiService] API call failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Extracts the text content from Gemini's JSON response structure.
     */
    private String extractTextFromGeminiResponse(String responseBody) {
        try {
            JsonNode root    = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts   = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
        } catch (Exception e) {
            System.err.println("[GeminiService] Failed to parse response: " + e.getMessage());
        }
        return null;
    }
}
