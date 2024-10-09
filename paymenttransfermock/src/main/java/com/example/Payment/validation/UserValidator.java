package com.example.Payment.validation;

import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    // Example validation methods (implement as needed)
    public static boolean isValidUserId(String userId) {
        return userId != null && !userId.trim().isEmpty(); // Basic validation, can be enhanced
    }

}
