package com.financeapp.personal_finance_tool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Method to create a new user
    public int createUser(User user) throws SQLException {
    String sql = "INSERT INTO users (username, password, email, created_at) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPasswordHash());
        stmt.setString(3, user.getEmail());
        stmt.setDate(4, user.getCreatedDate());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }
        // Retrieve the generated ID
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);  // Return the generated User ID
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }
}

    // Method to retrieve a user by username
//    public User getUserByUsername(String username) throws SQLException {
//        String sql = "SELECT * FROM users WHERE username = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, username);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return new User(rs.getInt("user_id"), rs.getString("username"),
//                                    rs.getString("password"), rs.getString("email"));
//                }
//            }
//        }
//        return null;  // User not found
//    }
    public int getUserIdByUsername(String username) throws SQLException {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return -1; // Return -1 if user not found
    }
        public boolean authenticateUser(String username, String password, int userId) throws SQLException {
        String query = "SELECT password FROM users WHERE username = ? AND user_id = ? ";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, userId);
            

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Retrieve the stored password
                String storedPassword = rs.getString("password");

                // Compare the entered password with the stored password (plain-text comparison)
                return storedPassword.equals(password);
            }
        }
        return false;  // Return false if user not found
    }

    }

