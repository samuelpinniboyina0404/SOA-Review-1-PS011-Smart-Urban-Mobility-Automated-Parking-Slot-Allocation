package com.klu.jwtdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("access", "PUBLIC");
        response.put("status", "SUCCESS");
        response.put("message", "This is an unauthenticated public endpoint. Anyone can access this!");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> userEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("access", "PROTECTED (USER / ADMIN)");
        response.put("status", "SUCCESS");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("message", "Welcome, authenticated user! You successfully presented a valid JWT token.");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("access", "RESTRICTED (ADMIN ONLY)");
        response.put("status", "SUCCESS");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("message", "⚡ High-privilege Admin Endpoint accessed! Your JWT token has ROLE_ADMIN permissions.");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
