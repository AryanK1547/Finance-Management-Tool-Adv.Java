package com.financeapp.personal_finance_tool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Method to create a new user
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, created_at) VALUES (?, ?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3,user.getEmail());
            stmt.setDate(4, user.getCreatedDate());
            stmt.executeUpdate();
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
    public boolean authenticateUser(String username, String password,int user_id ) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND user_id= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, user_id);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Returns true if user exists, false otherwise
        }
    }
}
