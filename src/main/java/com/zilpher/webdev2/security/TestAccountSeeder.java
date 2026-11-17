package com.zilpher.webdev2.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.zilpher.webdev2.entity.User;
import com.zilpher.webdev2.repository.UserRepository;

@Component
public class TestAccountSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_SEED_MANAGER_PASSWORD:}")
    private String managerPassword;

    @Value("${APP_SEED_READER_PASSWORD:}")
    private String readerPassword;

    public TestAccountSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (managerPassword != null && !managerPassword.isBlank()) {
            seedUser("manager_demo", "manager.demo@example.test", "MANAGER", managerPassword);
        }
        if (readerPassword != null && !readerPassword.isBlank()) {
            seedUser("reader_demo", "reader.demo@example.test", "READER", readerPassword);
        }
    }

    private void seedUser(String username, String email, String role, String rawPassword) {
        User user = userRepository.findByUsername(username).orElseGet(User::new);
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }
}