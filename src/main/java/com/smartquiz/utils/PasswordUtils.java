package com.smartquiz.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt
 */
public class PasswordUtils {

    /**
     * Hash a password using BCrypt
     * @param password Plain text password
     * @return Hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Verify a password against its hash
     * @param password Plain text password
     * @param hashedPassword Hashed password from database
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a password meets minimum security requirements
     * @param password Password to validate
     * @return true if password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");

        return hasLetter && hasNumber;
    }

    /**
     * Get password strength description
     * @param password Password to evaluate
     * @return String describing password strength
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Too short (minimum 6 characters)";
        }

        int score = 0;

        // Length
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Character types
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;

        return switch (score) {
            case 0, 1, 2 -> "Weak";
            case 3, 4 -> "Medium";
            case 5, 6 -> "Strong";
            default -> "Very Strong";
        };
    }
}