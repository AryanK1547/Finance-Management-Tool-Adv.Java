package com.financeapp.personal_finance_tool;

import java.sql.Date;

public class Transaction {
	private int loggedInUserId;

    public Transaction(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        // Other initialization code
    }
	LoginGUI login ;
    private int id;
    private double amount;
    private String description;
    private String category;
    private java.sql.Date transactionDate;
    private int userId;  // Add userId to the Transaction class
    private User user;

    // Constructor with userId
  

    // Getters and setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }  
    
    public Transaction(int id, double amount, String description, String category, java.sql.Date transactionDate, int userId) {
    
         // Assuming `LoginGUI` provides this after successful login
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.transactionDate = transactionDate;
       this.loggedInUserId = UserSession.getInstance().getUserId();;
        this.userId =loggedInUserId;// Assign userId
       // System.out.println(userId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public java.sql.Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(java.sql.Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
