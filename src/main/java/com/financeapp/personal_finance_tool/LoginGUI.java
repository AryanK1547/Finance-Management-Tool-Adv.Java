package com.financeapp.personal_finance_tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginGUI {
    private JFrame frame;
    private UserDAO userDAO;
    private LoginCallback loginCallback;  // Callback for login completion


    public LoginGUI(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
        try {
            userDAO = new UserDAO(DBConnection.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

    private void initialize() {
        JDialog dialog = new JDialog(frame, "Login", true);
        dialog.setLayout(new GridLayout(4, 2));

        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField();
        JLabel lblUserId = new JLabel("User ID:");
        JTextField txtUserId = new JTextField();
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();
        JButton btnLogin = new JButton("Login");

        dialog.add(lblUsername);
        dialog.add(txtUsername);
        dialog.add(lblUserId);
        dialog.add(txtUserId);
        dialog.add(lblPassword);
        dialog.add(txtPassword);
        dialog.add(new JLabel());  // Empty for alignment
        dialog.add(btnLogin);

        // Action listener for the Login button
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                int userId = Integer.parseInt(txtUserId.getText());

                try {
                    boolean isAuthenticated = userDAO.authenticateUser(username, password, userId);
                    if (isAuthenticated) {
                        JOptionPane.showMessageDialog(frame, "Login successful!");
                        dialog.dispose();
                        loginCallback.onLoginComplete(userId);  // Notify that login is complete
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid login. Please check your credentials.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error during login: " + ex.getMessage());
                }
            }
        });

        dialog.setSize(300, 200);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginGUI(userId -> {
            UserSession.getInstance().setUserId(userId);  // Store user ID
            new FinanceManagerGUI();  // Pass to GUI if needed
        });
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
}
