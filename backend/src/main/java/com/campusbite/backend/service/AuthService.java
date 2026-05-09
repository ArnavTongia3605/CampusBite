package com.campusbite.backend.service;

import com.campusbite.backend.dto.Dtos.AuthRequest;
import com.campusbite.backend.dto.Dtos.AuthResponse;
import com.campusbite.backend.entity.User;
import com.campusbite.backend.repository.UserRepository;
import com.campusbite.backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service handling user registration and login business logic.
 */
@Service
public class AuthService {

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtUtil              jwtUtil;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
        this.authManager     = authManager;
    }

    /**
     * Registers a new user after checking for duplicate usernames.
     */
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken. Please choose another.");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(user);

        return new AuthResponse(null, user.getUsername(), "User registered successfully.");
    }

    /**
     * Authenticates user credentials and returns a JWT token.
     */
    public AuthResponse login(AuthRequest request) {
        // Spring Security handles validation
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token, request.getUsername(), "Login successful.");
    }
}
