package com.parkgrid.gateway;

import com.parkgrid.gateway.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
public class ApiGatewayTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testJwtUtilBeanInitialization() {
        assertNotNull(jwtUtil);
    }
}
