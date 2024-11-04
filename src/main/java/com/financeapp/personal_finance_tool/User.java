package com.financeapp.personal_finance_tool;

import java.sql.Date;
import java.util.regex.Pattern;

public class User {
    private int userId;
    private String username;
    private String passwordHash; // Store hashed password
    private String email;
    private Date createdDate; // Use java.sql.Date for SQL compatibility

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    // Constructor with proper date handling
    public User(int userId, String username, String password, String email, Date createdDate) {
        this.userId = userId;
        this.username = username;
        setPassword(password); // Set hashed password
        setEmail(email);       // Set validated email
        this.createdDate = createdDate; // Ensure this is a java.sql.Date
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    // Setters with basic validation
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username;
        } else {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
    }

    public void setPassword(String password) {
        if (password != null && password.length() >= 8) {
            this.passwordHash = hashPassword(password); // Store hashed password
        } else {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
    }

    public void setEmail(String email) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    // Hashing password (example method, you'd want to use a real hashing algorithm)
    private String hashPassword(String password) {
        // This is a simple example; for production use, use a stronger hashing algorithm (e.g., BCrypt or SHA)
        return Integer.toHexString(password.hashCode());
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
