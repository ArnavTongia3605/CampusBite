# CampusBite

CampusBite is an AI-powered campus food recommendation web application that helps students discover the best meals on campus based on their mood, budget, food type, and veg/non-veg preferences.

The application combines a modern frontend with a Spring Boot backend, PostgreSQL database, JWT authentication, and Google Gemini AI for smart personalized food recommendations.

---

# Features

- User Registration & Login with JWT Authentication
- Browse Campus Food Outlets and Menus
- AI-Powered Food Recommendations using Gemini API
- Budget-based meal suggestions
- Mood-based recommendations (Spicy, Comfort, Sweet, etc.)
- Veg / Non-Veg filtering
- Real-time frontend-backend integration
- PostgreSQL database integration
- Secure Spring Security configuration with CORS support

---

# Tech Stack

| Category | Technologies |
|----------|--------------|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java, Spring Boot |
| Database | PostgreSQL |
| Authentication | Spring Security + JWT |
| AI Integration | Google Gemini API |
| ORM | Spring Data JPA |
| API Testing | Postman |
| Build Tool | Maven |

---
# Project Structure

```bash
CampusBite/
│
├── frontend/
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── recommendation.html
│   ├── css/
│   ├── js/
│   └── assets/
│
└── backend/
    ├── pom.xml
    ├── src/
    │   ├── main/
    │   │   ├── java/com/campusbite/backend/
    │   │   └── resources/
    │   └── test/
    ├── mvnw
    └── mvnw.cmd
```

---

# Key Functionalities

## Authentication System

- User registration and login
- Password encryption using BCrypt
- JWT token generation and validation
- Protected API endpoints

---

## Food Recommendation Engine

Users can select:

- Budget
- Mood/Craving
- Meal Type
- Veg/Non-Veg Preference

The backend filters food items from the database and Gemini AI generates personalized recommendation reasons.

---

## Gemini AI Integration

CampusBite uses Google Gemini API to:

- Generate smart recommendation explanations
- Personalize food suggestions
- Improve user experience with AI-generated responses

Example:

> “Kadhai Paneer is a perfect spicy dinner option within your budget.”

---

# Database Design

Main table used:

## `food_items`

| Column | Description |
|--------|-------------|
| id | Primary Key |
| outlet_name | Food outlet name |
| food_name | Dish name |
| price | Food price |
| mood_tag | Mood category |
| food_type | Breakfast/Lunch/Dinner/Snacks |
| is_veg | Veg or Non-Veg |

---

# Setup Instructions

## 1. Clone the Repository

```bash
git clone https://github.com/your-username/CampusBite.git
cd CampusBite
```

---

# 2. Backend Setup

Open the `backend` folder in IntelliJ IDEA.

## Configure PostgreSQL

Create a database:

```sql
CREATE DATABASE campusbite;
```

---

## Update `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/campusbite
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

jwt.secret=your_jwt_secret

gemini.api.key=your_gemini_api_key
```

---

## Run Spring Boot Backend

```bash
./mvnw spring-boot:run
```

Backend runs on:

```bash
http://localhost:8080
```

---

# 3. Frontend Setup

Open the `frontend` folder in VS Code.

Run using Live Server extension.

Frontend runs on:

```bash
http://127.0.0.1:5500
```

---

# API Endpoints

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/auth/register` | Register User |
| POST | `/api/auth/login` | Login User |
| GET | `/api/outlets` | Get Outlet Menus |
| POST | `/api/recommendations` | Get AI Recommendations |

---

# Sample Recommendation Flow

1. User logs in

2. Selects:
   - Budget
   - Mood
   - Food Type
   - Veg/Non-Veg

3. Frontend sends request to backend

4. Spring Boot fetches matching items from PostgreSQL

5. Gemini AI generates recommendation reasons

6. Results are displayed dynamically on UI

---

# Future Improvements

- Mobile Responsive UI
- Better AI Personalization
- Live Queue & Availability Tracking
- Food Ratings & Reviews
- Online Ordering Integration
- Cloud Deployment
- Smart Notifications

---

# Challenges Solved

- Frontend ↔ Backend integration
- CORS configuration
- JWT Authentication handling
- PostgreSQL table mapping
- Dynamic recommendation rendering
- Gemini API integration
- Veg/Non-Veg filtering bugs

---

# Author

Arnav Tongia
