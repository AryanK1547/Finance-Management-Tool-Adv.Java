package com.financeapp.personal_finance_tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardGUI extends JFrame {
    private JPanel mainPanel;
    private JButton transactionButton, budgetButton, reportsButton, logoutButton;
    private JLabel titleLabel, usernameLabel;
    private ImageIcon profileIcon;
    private JPanel centerPanel;

    public DashboardGUI() {
        // Frame settings
        setTitle("Finance Management Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Main Panel (Top bar and content)
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Top Bar Panel
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
        topBar.setBackground(new Color(30, 30, 30));  // Dark background

        // Title Label (Modern Font and Color)
        titleLabel = new JLabel("Finance Management Tool");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.CENTER);

        // Right side of Top Bar (Profile Picture & Username)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(topBar.getBackground());

        profileIcon = new ImageIcon("path/to/profile_icon.png");  // Replace with actual image path
        JLabel profileLabel = new JLabel(profileIcon);

        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usernameLabel.setForeground(Color.WHITE);
        
        rightPanel.add(profileLabel);
        rightPanel.add(usernameLabel);

        // Logout Button with Modern Style
        logoutButton = createRoundedButton("Logout", new Color(255, 100, 100), Color.WHITE);
        topBar.add(rightPanel, BorderLayout.EAST);
        topBar.add(logoutButton, BorderLayout.WEST);

        // Sidebar Menu (Rounded buttons with modern colors)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 10, 10));
        sidebar.setBackground(new Color(40, 40, 40));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        transactionButton = createRoundedButton("Transactions", new Color(0, 128, 128), Color.WHITE);
        budgetButton = createRoundedButton("Budgeting", new Color(34, 193, 195), Color.WHITE);
        reportsButton = createRoundedButton("Reports", new Color(0, 102, 204), Color.WHITE);

        sidebar.add(transactionButton);
        sidebar.add(budgetButton);
        sidebar.add(reportsButton);

        // Center Panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome to Finance Management Tool");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add components to the main frame
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        // Add action listeners
        transactionButton.addActionListener(e -> showTransactionPanel());

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        return button;
    }

    private void showTransactionPanel() {
        centerPanel.removeAll();

        JPanel transactionPanel = new JPanel();
        transactionPanel.setLayout(new GridLayout(1, 2, 10, 10));
        transactionPanel.setBackground(Color.WHITE);

        JButton addTransactionButton = createRoundedButton("Add Transaction", new Color(0, 128, 128), Color.WHITE);
        JButton viewTransactionButton = createRoundedButton("View Transactions", new Color(34, 193, 195), Color.WHITE);

        transactionPanel.add(addTransactionButton);
        transactionPanel.add(viewTransactionButton);

        centerPanel.add(transactionPanel, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardGUI dashboardGUI = new DashboardGUI();
            dashboardGUI.setVisible(true);
        });
    }
}
