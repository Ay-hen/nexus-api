
package com.nexus.app.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nexus.app.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Map<String, Object> register(
            String name, String email, String password) {

        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User(
                name.trim(),
                normalizedEmail,
                passwordEncoder.encode(password));

        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return Map.of(
                "message", "Registration successful",
                "token", token,
                "tokenType", "Bearer",
                "userId", user.getId(),
                "email", user.getEmail());
    }

    public Map<String, Object> login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return Map.of(
                "message", "Login successful",
                "token", token,
                "tokenType", "Bearer",
                "userId", user.getId(),
                "email", user.getEmail());
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email is required");
        }

        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
