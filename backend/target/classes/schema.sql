DROP TABLE IF EXISTS food_items;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE food_items (
                            id BIGSERIAL PRIMARY KEY,
                            outlet_name VARCHAR(100) NOT NULL,
                            food_name VARCHAR(150) NOT NULL,
                            price DOUBLE PRECISION NOT NULL,
                            mood_tag VARCHAR(50),
                            food_type VARCHAR(50),
                            is_veg BOOLEAN,
                            category VARCHAR(50)
);