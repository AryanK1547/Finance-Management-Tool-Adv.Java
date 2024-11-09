//package com.financeapp.personal_finance_tool;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.LineBorder;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.SQLException;
//
//public class LoginGUI {
//    private JFrame frame;
//    private UserDAO userDAO;
//    private LoginCallback loginCallback;
//
//    public LoginGUI(LoginCallback loginCallback) {
//        this.loginCallback = loginCallback;
//        try {
//            userDAO = new UserDAO(DBConnection.getConnection());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        initialize();
//    }
//
//    private void initialize() {
//        // Frame settings
//        frame = new JFrame("Personal Finance Tool - Login");
//        frame.setSize(400, 500);
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(new BorderLayout());
//
//        // Background color for the frame
//        frame.getContentPane().setBackground(new Color(240, 240, 245));
//
//        // Main panel with padding and rounded borders
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
//        mainPanel.setBackground(new Color(255, 255, 255));
//        mainPanel.setBorder(new LineBorder(new Color(210, 210, 210), 1, true));
//
//        JLabel lblTitle = new JLabel("Welcome Back");
//        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
//        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
//        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
//        
//        JLabel lblSubtitle = new JLabel("Login to your account");
//        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
//        lblSubtitle.setForeground(new Color(120, 120, 120));
//        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
//        lblSubtitle.setBorder(new EmptyBorder(0, 0, 20, 0));
//
//        // Username and Password fields with rounded borders
//        JTextField txtUsername = new JTextField();
//        txtUsername.setBorder(new RoundedBorder(10));
//        txtUsername.setPreferredSize(new Dimension(200, 35));
//        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 14));
//        txtUsername.setMargin(new Insets(5, 10, 5, 10));
//
//        JTextField txtUserId = new JTextField();
//        txtUserId.setBorder(new RoundedBorder(10));
//        txtUserId.setPreferredSize(new Dimension(200, 35));
//        txtUserId.setFont(new Font("SansSerif", Font.PLAIN, 14));
//        txtUserId.setMargin(new Insets(5, 10, 5, 10));
//
//        JPasswordField txtPassword = new JPasswordField();
//        txtPassword.setBorder(new RoundedBorder(10));
//        txtPassword.setPreferredSize(new Dimension(200, 35));
//        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
//        txtPassword.setMargin(new Insets(5, 10, 5, 10));
//
//        // Login button with rounded design
//        JButton btnLogin = new JButton("Login");
//        btnLogin.setPreferredSize(new Dimension(200, 40));
//        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
//        btnLogin.setBackground(new Color(52, 152, 219));
//        btnLogin.setForeground(Color.WHITE);
//        btnLogin.setFocusPainted(false);
//        btnLogin.setBorder(new RoundedBorder(20));
//        
//        // Login button action listener
//        btnLogin.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String username = txtUsername.getText();
//                String password = new String(txtPassword.getPassword());
//                int userId = Integer.parseInt(txtUserId.getText());
//
//                try {
//                    boolean isAuthenticated = userDAO.authenticateUser(username, password, userId);
//                    if (isAuthenticated) {
//                        JOptionPane.showMessageDialog(frame, "Login successful!");
//                        frame.dispose();
//                        loginCallback.onLoginComplete(userId);  // Notify that login is complete
//                    } else {
//                        JOptionPane.showMessageDialog(frame, "Invalid login. Please check your credentials.");
//                    }
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(frame, "Error during login: " + ex.getMessage());
//                }
//            }
//        });
//
//        // Add components to main panel
//        mainPanel.add(lblTitle);
//        mainPanel.add(lblSubtitle);
//        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        mainPanel.add(createLabeledField("Username", txtUsername));
//        mainPanel.add(createLabeledField("User ID", txtUserId));
//        mainPanel.add(createLabeledField("Password", txtPassword));
//        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        mainPanel.add(btnLogin);
//
//        frame.add(mainPanel, BorderLayout.CENTER);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//
//    private JPanel createLabeledField(String labelText, JComponent field) {
//        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout());
//        panel.setBorder(new EmptyBorder(0, 0, 5, 0));
//        panel.setBackground(Color.WHITE);
//
//        JLabel label = new JLabel(labelText);
//        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
//        label.setForeground(new Color(120, 120, 120));
//
//        panel.add(label, BorderLayout.NORTH);
//        panel.add(field, BorderLayout.CENTER);
//        return panel;
//    }
//
//    // Custom rounded border class
//    class RoundedBorder extends LineBorder {
//        private int radius;
//
//        public RoundedBorder(int radius) {
//            super(new Color(200, 200, 200), 1, true);
//            this.radius = radius;
//        }
//
//        @Override
//        public Insets getBorderInsets(Component c) {
//            return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
//        }
//
//        @Override
//        public boolean isBorderOpaque() {
//            return false;
//        }
//    }
//
//    public static void main(String[] args) {
//        new LoginGUI(userId -> {
//            UserSession.getInstance().setUserId(userId);  // Store user ID
//            DashboardGUI dashboardGUI = new DashboardGUI();  // Open DashboardGUI after login
//            dashboardGUI.setVisible(true);
//        });
//    }
//}
