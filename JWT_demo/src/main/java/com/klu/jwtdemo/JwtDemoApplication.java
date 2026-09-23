package com.klu.jwtdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JwtDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtDemoApplication.class, args);
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("🚀 JWT Demo Microservice Application Started Successfully!");
        System.out.println("🌐 Web Dashboard: http://localhost:8080");
        System.out.println("🗄️ H2 Console:   http://localhost:8080/h2-console");
        System.out.println("-------------------------------------------------------------\n");
    }
}
