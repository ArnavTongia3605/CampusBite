package com.campusbite.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a registered user in the system.
 * Maps to the 'users' table in PostgreSQL.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password; // BCrypt hashed

    @Column(nullable = false)
    private String role = "ROLE_USER";

    // ---- Constructors ----
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ---- Getters & Setters ----
    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getUsername()            { return username; }
    public void setUsername(String u)      { this.username = u; }

    public String getPassword()            { return password; }
    public void setPassword(String p)      { this.password = p; }

    public String getRole()                { return role; }
    public void setRole(String role)       { this.role = role; }
}
