package com.parkgrid.auth.config;

import com.parkgrid.auth.entity.User;
import com.parkgrid.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@parkgrid.com")) {
            User admin = new User(
                "ParkGrid Admin",
                "admin@parkgrid.com",
                passwordEncoder.encode("Admin@123"),
                "ROLE_ADMIN"
            );
            userRepository.save(admin);
            System.out.println(">>> Seeded default admin account: admin@parkgrid.com / Admin@123");
        }

        if (!userRepository.existsByEmail("user@parkgrid.com")) {
            User driver = new User(
                "Demo Driver",
                "user@parkgrid.com",
                passwordEncoder.encode("User@123"),
                "ROLE_USER"
            );
            userRepository.save(driver);
            System.out.println(">>> Seeded default user account: user@parkgrid.com / User@123");
        }
    }
}
