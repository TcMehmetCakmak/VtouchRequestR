package com.venhancer.vmerge.config;

import com.venhancer.vmerge.entity.User;
import com.venhancer.vmerge.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * DataLoader component that automatically creates demo users for testing
 * Only runs in development profiles (dev, local)
 */
@Component
@Profile({"dev", "local"})
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Loading demo data...");
        
        // Check if demo data already exists
        if (userRepository.count() > 0) {
            logger.info("Demo data already exists. Skipping data loading.");
            return;
        }

        createDemoUsers();
        logger.info("Demo data loading completed successfully!");
    }

    private void createDemoUsers() {
        List<User> demoUsers = Arrays.asList(
            createUser("admin", "admin@example.com", "Admin", "User", "admin123", User.UserRole.ADMIN, User.UserStatus.ACTIVE),
            createUser("user", "user@example.com", "Regular", "User", "user123", User.UserRole.USER, User.UserStatus.ACTIVE),
            createUser("moderator", "moderator@example.com", "Moderator", "User", "mod123", User.UserRole.MODERATOR, User.UserStatus.ACTIVE),
            createUser("john.doe", "john.doe@example.com", "John", "Doe", "user123", User.UserRole.USER, User.UserStatus.ACTIVE),
            createUser("jane.smith", "jane.smith@example.com", "Jane", "Smith", "mod123", User.UserRole.MODERATOR, User.UserStatus.ACTIVE),
            createUser("inactive", "inactive@example.com", "Inactive", "User", "user123", User.UserRole.USER, User.UserStatus.INACTIVE)
        );

        userRepository.saveAll(demoUsers);
        
        logger.info("Created {} demo users:", demoUsers.size());
        demoUsers.forEach(user -> 
            logger.info("  - {} ({}): {} - Active: {}", 
                user.getUsername(), 
                user.getEmail(), 
                user.getRole(), 
                user.isActive())
        );
    }

    private User createUser(String username, String email, String firstName, String lastName, 
                           String password, User.UserRole role, User.UserStatus status) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(status);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
} 