package com.financeapp.personal_finance_tool;

public class UserSession {
    private static UserSession instance;
    private int userId;
    private String username;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
    
    public void clearSession() {
        userId = 0;
        username = null;
    }
}
