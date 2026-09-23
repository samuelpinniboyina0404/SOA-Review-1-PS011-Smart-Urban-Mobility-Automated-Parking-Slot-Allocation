package com.parkgrid.auth;

import com.parkgrid.auth.dto.AuthResponse;
import com.parkgrid.auth.dto.LoginRequest;
import com.parkgrid.auth.dto.RegisterRequest;
import com.parkgrid.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    public void testRegisterAndLogin() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Test Driver",
                "testdriver@parkgrid.com",
                "Password@123",
                "ROLE_USER"
        );

        AuthResponse regResponse = authService.register(registerRequest);
        assertNotNull(regResponse);
        assertNotNull(regResponse.getToken());
        assertEquals("testdriver@parkgrid.com", regResponse.getUser().getEmail());

        LoginRequest loginRequest = new LoginRequest("testdriver@parkgrid.com", "Password@123");
        AuthResponse loginResponse = authService.login(loginRequest);
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
    }

    @Test
    public void testDefaultAdminLogin() {
        LoginRequest loginRequest = new LoginRequest("admin@parkgrid.com", "Admin@123");
        AuthResponse loginResponse = authService.login(loginRequest);
        assertNotNull(loginResponse);
        assertEquals("ROLE_ADMIN", loginResponse.getUser().getRole());
    }
}
