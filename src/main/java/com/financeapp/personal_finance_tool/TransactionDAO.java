package com.financeapp.personal_finance_tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

	public void addTransaction(Transaction transaction) throws SQLException {
	    String query = "INSERT INTO transactions (amount, description, category, transaction_date, user_id) VALUES (?, ?, ?, ?, ?)";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setDouble(1, transaction.getAmount());
	        pstmt.setString(2, transaction.getDescription());
	        pstmt.setString(3, transaction.getCategory());
	        pstmt.setDate(4, transaction.getTransactionDate());
//	        System.out.println(transaction.getUserId());
	        pstmt.setInt(5, UserSession.getInstance().getUserId());  // Set the userId
	        pstmt.executeUpdate();
	    }
	}


	public List<Transaction> getAllTransactions(int userId) throws SQLException {
	    List<Transaction> transactions = new ArrayList<>();
	    String query = "SELECT * FROM transactions WHERE user_id = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, userId);  // Set userId in the prepared statement
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Transaction transaction = new Transaction(
	                    rs.getInt("id"),
	                    rs.getDouble("amount"),
	                    rs.getString("description"),
	                    rs.getString("category"),
	                    rs.getDate("transaction_date"),
	                    rs.getInt("user_id")
	                );
	                transactions.add(transaction);
	            }
	        }
	    }
	    return transactions;
	}

    public void updateTransaction(Transaction transaction) throws SQLException {
        String query = "UPDATE transactions SET amount = ?, description = ?, category = ?, transaction_date = ? WHERE id = ? AND user_id= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setString(2, transaction.getDescription());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setDate(4, transaction.getTransactionDate());
            pstmt.setInt(5, transaction.getId());
            pstmt.setInt(6,transaction.getUserId());
            pstmt.executeUpdate();
        }
    }

    public void deleteTransaction(int id, int userId) throws SQLException {
        String query = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);       // Set the transaction ID
            pstmt.setInt(2, userId);   // Set the user ID
            int rowsAffected = pstmt.executeUpdate();
           if (rowsAffected == 0) {
                System.out.println("No transaction found for the given ID and user.");
            }
        }
    }
}
