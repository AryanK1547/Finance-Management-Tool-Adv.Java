//package com.financeapp.personal_finance_tool;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.SQLException;
//
//public class SignUpGUI {
//    private JFrame frame;
//    private UserDAO userDAO;  // For interacting with the database
//
//    public SignUpGUI() {
//        try {
//            userDAO = new UserDAO(DBConnection.getConnection());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        initialize();
//    }
//
//    private void initialize() {
//        frame = new JFrame("Sign Up");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 300);
//        frame.setLayout(new GridLayout(6, 2)); // Adjusted to fit the layout properly
//
//        // Components for the sign-up form
//        JLabel lblUsername = new JLabel("Username:");
//        JTextField txtUsername = new JTextField();
//
//        JLabel lblPassword = new JLabel("Password:");
//        JPasswordField txtPassword = new JPasswordField();
//
//        JLabel lblEmail = new JLabel("Email:");
//        JTextField txtEmail = new JTextField();
//
//        JButton btnSignUp = new JButton("Sign Up");
//        JLabel lblLoginPage = new JLabel("Already Have an Account? Log In.");
//
//        // Add components to the frame
//        frame.add(lblUsername);
//        frame.add(txtUsername);
//        frame.add(lblPassword);
//        frame.add(txtPassword);
//        frame.add(lblEmail);
//        frame.add(txtEmail);
//        frame.add(new JLabel());  // Empty label for alignment
//        frame.add(btnSignUp);
//        frame.add(lblLoginPage);
//
//        // Action listener for the "Sign Up" button
//        btnSignUp.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Sign-Up button clicked"); // Debugging statement
//
//                try {
//                    String username = txtUsername.getText();
//                    String password = new String(txtPassword.getPassword());
//                    String email = txtEmail.getText();
//
//                    // Ensure fields are not empty
//                    if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
//                        JOptionPane.showMessageDialog(frame, "All fields are required.");
//                        return;
//                    }
//
//                    // Create new User object with the current date
//                    java.util.Date utilDate = new java.util.Date();
//                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//
//                    User newUser = new User(0, username, password, email, sqlDate);
//                    UserDAO userDAO = new UserDAO(DBConnection.getConnection());
//                    userDAO.createUser(newUser);
//
//                    System.out.println("User created: " + username); // Debugging statement
//
//                    JOptionPane.showMessageDialog(frame, "Registration successful! You can now log in.");
//                    frame.dispose(); // Close the dialog after successful registration
//                    new LoginGUI(userId -> {
//                        UserSession.getInstance().setUserId(userId);  // Store user ID
//                        System.out.println("User ID looged in: " + userId); // Debugging statement
//
//                        new FinanceManagerGUI();  // Pass to GUI if needed
//                    });
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
//                    System.out.println("SQLException: " + ex.getMessage()); // Debugging statement
//                }
//            }
//        });
//        frame.setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(SignUpGUI::new);
//    }
//
//
//}
//
