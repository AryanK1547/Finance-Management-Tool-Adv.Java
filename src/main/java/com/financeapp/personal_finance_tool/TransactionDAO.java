package com.financeapp.personal_finance_tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

 
  public List<Transaction> getAllTransactions(int userId, String sortBy, String sortOrder, LocalDate selectedDate) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    
    // Default values for sorting if not provided
    if (sortBy == null || sortBy.isEmpty()) {
        sortBy = "transaction_date";
        sortOrder = "ASC";
    }

    // Build query with optional date filtering
    String query = "SELECT * FROM transactions WHERE user_id = ? AND transaction_date = ? ORDER BY " + sortBy + " " + sortOrder;
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, userId);  // Set userId in the prepared statement
        pstmt.setDate(2, java.sql.Date.valueOf(selectedDate)); // Set the selected date as a parameter
        
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
  public List<Transaction> getFilteredTransactions(int userId, int year, int month, int day) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ? AND DAY(transaction_date) = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, userId);
        pstmt.setInt(2, year);
        pstmt.setInt(3, month);
        pstmt.setInt(4, day);

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
  public List<Integer> getDistinctYears(int userId) throws SQLException {
    List<Integer> distinctYears = new ArrayList<>();
    String query = "SELECT DISTINCT YEAR(transaction_date) FROM transactions WHERE user_id = ? ORDER BY YEAR(transaction_date) DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, userId); // Set the userId in the prepared statement
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                distinctYears.add(rs.getInt(1)); // Add the distinct year to the list
            }
        }
    }

    return distinctYears;
}
  public List<Transaction> getFilteredTransactionsByYear(int userId, int year) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND YEAR(transaction_date) = ?";
    
     try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, userId);
        pstmt.setInt(2, year);
        
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
    return transactions;
}



    }
  public List<Transaction> getFilteredTransactionsByMonth(int userId, int month) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND MONTH(transaction_date) = ?";
    
   try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, userId);
        pstmt.setInt(2, month);
        
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
  public List<Transaction> getFilteredTransactionsByDay(int userId, int day) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND DAY(transaction_date) = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, userId);
        pstmt.setInt(2, day);
        
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
  public List<Transaction> getFilteredTransactionsByYearAndMonth(int userId, int year, int month) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ?";
    
   try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)){
        
        pstmt.setInt(1, userId);
        pstmt.setInt(2, year);
        pstmt.setInt(3, month);
        
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
  public List<Transaction> getFilteredTransactionsByYearAndDay(int userId, int year, int day) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND YEAR(transaction_date) = ? AND DAY(transaction_date) = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)){
        
        pstmt.setInt(1, userId);
        pstmt.setInt(2, year);
        pstmt.setInt(3, day);
        
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
  public List<Transaction> getFilteredTransactionsByMonthAndDay(int userId, int month, int day) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND MONTH(transaction_date) = ? AND DAY(transaction_date) = ?";
   try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, userId);
        pstmt.setInt(2, month);
        pstmt.setInt(3, day);
        
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
  public List<Transaction> getFilteredTransactionsByYearMonthAndDay(int userId, int year, int month, int day) throws SQLException {
    List<Transaction> transactions = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE user_id = ? AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ? AND DAY(transaction_date) = ?";
    
   try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)){
        
        pstmt.setInt(1, userId);
        pstmt.setInt(2, year);
        pstmt.setInt(3, month);
        pstmt.setInt(4, day);
        
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


}
    



