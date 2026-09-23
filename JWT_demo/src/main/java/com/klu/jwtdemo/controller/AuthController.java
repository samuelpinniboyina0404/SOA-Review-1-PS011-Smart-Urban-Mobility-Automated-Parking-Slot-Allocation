package com.klu.jwtdemo.controller;

import com.klu.jwtdemo.dto.AuthRequest;
import com.klu.jwtdemo.dto.AuthResponse;
import com.klu.jwtdemo.model.UserEntity;
import com.klu.jwtdemo.repository.UserRepository;
import com.klu.jwtdemo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Username '" + request.getUsername() + "' is already taken.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        String role = request.getRole() != null && !request.getRole().trim().isEmpty() 
                      ? request.getRole().trim().toUpperCase() 
                      : "USER";

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        UserEntity user = new UserEntity(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                role
        );

        userRepository.save(user);

        Map<String, String> res = new HashMap<>();
        res.put("message", "User registered successfully!");
        res.put("username", user.getUsername());
        res.put("role", user.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserEntity user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwtToken = jwtUtil.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(new AuthResponse(
                    jwtToken,
                    user.getUsername(),
                    user.getRole(),
                    "Authentication successful!"
            ));
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String username = auth.getName();
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());
        profile.put("authorities", auth.getAuthorities());
        profile.put("authenticated", true);

        return ResponseEntity.ok(profile);
    }
}
