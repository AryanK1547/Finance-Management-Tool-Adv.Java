package com.financeapp.personal_finance_tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/personal_finance";
    private static final String USER = "root";  // Replace with your MySQL username
    private static final String PASSWORD = "mysql";  // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}