package com.campusbite.backend.dto;

/**
 * DTOs (Data Transfer Objects) for CampusBite API communication.
 * Grouped in this file for simplicity — can be split into separate files.
 */
public class Dtos {

    // ===================== Auth DTOs =====================

    /** Request body for /api/auth/register and /api/auth/login */
    public static class AuthRequest {
        private String username;
        private String password;

        public AuthRequest() {}
        public String getUsername()        { return username; }
        public void setUsername(String u)  { this.username = u; }
        public String getPassword()        { return password; }
        public void setPassword(String p)  { this.password = p; }
    }

    /** Response body after successful login (contains JWT token) */
    public static class AuthResponse {
        private String token;
        private String username;
        private String message;

        public AuthResponse(String token, String username, String message) {
            this.token    = token;
            this.username = username;
            this.message  = message;
        }

        public String getToken()           { return token; }
        public String getUsername()        { return username; }
        public String getMessage()         { return message; }
    }

    // ===================== Recommendation DTOs =====================

    /** Request body for /api/recommendations */
    public static class RecommendationRequest {
        private int budget;
        private String mood;
        private String foodType;
        private Boolean isVeg;    // null = both

        public RecommendationRequest() {}
        public int getBudget()             { return budget; }
        public void setBudget(int b)       { this.budget = b; }
        public String getMood()            { return mood; }
        public void setMood(String m)      { this.mood = m; }
        public String getFoodType()        { return foodType; }
        public void setFoodType(String f)  { this.foodType = f; }
        public Boolean getIsVeg()          { return isVeg; }
        public void setIsVeg(Boolean v)    { this.isVeg = v; }
    }

    /** Single recommendation item in the response */
    public static class RecommendationItem {
        private String foodName;
        private String outletName;
        private double price;
        private boolean isVeg;
        private String reason;   // AI-generated explanation

        public RecommendationItem(String foodName, String outletName,
                                  double price, boolean isVeg, String reason) {
            this.foodName   = foodName;
            this.outletName = outletName;
            this.price      = price;
            this.isVeg      = isVeg;
            this.reason     = reason;
        }

        public String getFoodName()        { return foodName; }
        public String getOutletName()      { return outletName; }
        public double getPrice()           { return price; }
        public boolean isVeg()             { return isVeg; }
        public String getReason()          { return reason; }
    }

    /** API response wrapper */
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data    = data;
        }

        public boolean isSuccess()         { return success; }
        public String getMessage()         { return message; }
        public Object getData()            { return data; }
    }
}