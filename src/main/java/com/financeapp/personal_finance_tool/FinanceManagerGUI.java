package com.financeapp.personal_finance_tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class FinanceManagerGUI {
	private User user;
    private TransactionDAO transactionDAO;
    private JFrame frame;
    public static int loggedInUserId = -1;  // Initially set to -1 (not logged in)

    public FinanceManagerGUI() {
        this.loggedInUserId = UserSession.getInstance().getUserId();
        transactionDAO = new TransactionDAO();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Personal Finance Management Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Create components
        JButton btnAdd = new JButton("Add Transaction");
        JButton btnView = new JButton("View Transactions");
        JButton btnUpdate = new JButton("Update Transaction");
        JButton btnDelete = new JButton("Delete Transaction");
        JButton btnSignUp = new JButton("SignUP");
        JButton btnLogOut = new JButton("Log-Out");

        JPanel panel = new JPanel();

        // Add buttons to the panel
        panel.add(btnAdd);
        panel.add(btnView);
        panel.add(btnUpdate);
        panel.add(btnDelete);

       
        frame.add(btnSignUp,BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(btnLogOut, BorderLayout.SOUTH);


        btnLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Button Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddTransactionDialog();
            }
        });

        btnView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewTransactions();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateTransactionDialog();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteTransactionDialog();
            }
        });
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showSignUpDialog();
            }
        });

        frame.setVisible(true);
    }

    private void showSignUpDialog() {
        JDialog dialog = new JDialog(frame, "Sign Up", true);
        dialog.setLayout(new GridLayout(4, 2));

        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField();

        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();

        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField();

        JButton btnSignUp = new JButton("Sign Up");


        dialog.add(lblUsername);
        dialog.add(txtUsername);
        dialog.add(lblPassword);
        dialog.add(txtPassword);
        dialog.add(lblEmail);
        dialog.add(txtEmail);
        dialog.add(new JLabel());  // Empty for alignment
        dialog.add(btnSignUp);


        // Action listener for the Sign-Up button
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String username = txtUsername.getText();
                    String password = new String(txtPassword.getPassword());
                    String email = txtEmail.getText();

                    // Ensure fields are not empty
                    if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "All fields are required.");
                        return;
                    }

                    // Create new User object with the current date converted to java.sql.Date
                    java.util.Date utilDate = new java.util.Date();  // Get current date
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());  // Convert to java.sql.Date

                    User newUser = new User(0, username, password, email, sqlDate);
                    UserDAO userDAO = new UserDAO(DBConnection.getConnection());
                    userDAO.createUser(newUser);

                    JOptionPane.showMessageDialog(dialog, "Registration successful! You can now log in.");
                    dialog.dispose(); // Close the dialog after successful registration
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }
        });




        dialog.setSize(300, 200);
        dialog.setVisible(true);
    }

    

    private void showAddTransactionDialog() { 
    	
    JDialog dialog = new JDialog(frame, "Add Transaction", true);
    dialog.setLayout(new GridLayout(5, 2));
    
    JTextField txtAmount = new JTextField();
    JTextField txtDescription = new JTextField();
    JTextField txtCategory = new JTextField();
    JTextField txtDate = new JTextField(); // Format: yyyy-mm-dd

    dialog.add(new JLabel("Amount:"));
    dialog.add(txtAmount);
    dialog.add(new JLabel("Description:"));
    dialog.add(txtDescription);
    dialog.add(new JLabel("Category:"));
    dialog.add(txtCategory);
    dialog.add(new JLabel("Transaction Date (yyyy-mm-dd):"));
    dialog.add(txtDate);

    JButton btnAdd = new JButton("Add");
    dialog.add(btnAdd);
    btnAdd.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                String description = txtDescription.getText();
                String category = txtCategory.getText();
                java.sql.Date transactionDate = java.sql.Date.valueOf(txtDate.getText());

                Transaction transaction = new Transaction(0, amount, description, category, transactionDate,loggedInUserId);
                transactionDAO.addTransaction(transaction);
                JOptionPane.showMessageDialog(dialog, "Transaction added!");
                dialog.dispose();
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        }
    });

    dialog.setSize(300, 200);
    dialog.setVisible(true);
    }

    private void viewTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.getAllTransactions(loggedInUserId);
            StringBuilder sb = new StringBuilder();
            for (Transaction t : transactions) {
                sb.append(t.getId()).append(": ").append(t.getDescription())
                  .append(" - $").append(t.getAmount()).append(" (").append(t.getTransactionDate()).append(")\n");
            }
            JOptionPane.showMessageDialog(frame, sb.toString(), "Transactions for UserID: "+loggedInUserId, JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error retrieving transactions: " + e.getMessage());
        }
    }


    private void showUpdateTransactionDialog() {
        // Create a new dialog
        JDialog dialog = new JDialog(frame, "Update Transaction", true);

     
        dialog.setLayout(new GridLayout(6, 2));  // 5 rows, 2 columns

        // Labels and input fields for updating a transaction
        JLabel lblId = new JLabel("Transaction ID:");
        JTextField txtId = new JTextField();

        JLabel lblAmount = new JLabel("New Amount:");
        JTextField txtAmount = new JTextField();

        JLabel lblDescription = new JLabel("New Description:");
        JTextField txtDescription = new JTextField();

        JLabel lblCategory = new JLabel("New Category:");
        JTextField txtCategory = new JTextField();

        JLabel lblDate = new JLabel("New Transaction Date (yyyy-mm-dd):");
        JTextField txtDate = new JTextField();

        // Adding components in pairs (label and corresponding input field)
        dialog.add(lblId);
        dialog.add(txtId);
        dialog.add(lblAmount);
        dialog.add(txtAmount);
        dialog.add(lblDescription);
        dialog.add(txtDescription);
        dialog.add(lblCategory);
        dialog.add(txtCategory);
        dialog.add(lblDate);
        dialog.add(txtDate);

        // Add the Update button in a separate JPanel to avoid layout misalignment
        JPanel buttonPanel = new JPanel();
        JButton btnUpdate = new JButton("Update");
        buttonPanel.add(btnUpdate);
        dialog.add(new JLabel());  // Filler to keep layout balanced
        dialog.add(buttonPanel);

        // ActionListener for Update button
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(txtId.getText());
                    double amount = Double.parseDouble(txtAmount.getText());
                    String description = txtDescription.getText();
                    String category = txtCategory.getText();
                  
                    java.sql.Date transactionDate = java.sql.Date.valueOf(txtDate.getText());

                    Transaction transaction = new Transaction(id, amount, description, category, transactionDate,loggedInUserId);
                    transactionDAO.updateTransaction(transaction);
                    JOptionPane.showMessageDialog(dialog, "Transaction updated!");
                    dialog.dispose();
                } catch (SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }
        });

        dialog.setSize(400, 300);
        dialog.setVisible(true);
    }



    private void showDeleteTransactionDialog() {
        JDialog dialog = new JDialog(frame, "Delete Transaction", true);
        dialog.setLayout(new GridLayout(2, 2));
        
        JTextField txtId = new JTextField();
        
        dialog.add(new JLabel("Transaction ID:"));
        dialog.add(txtId);

        JButton btnDelete = new JButton("Delete");
        dialog.add(btnDelete);
        
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(txtId.getText());
                    transactionDAO.deleteTransaction(id,loggedInUserId);
                    JOptionPane.showMessageDialog(dialog, "Transaction deleted!");
                    dialog.dispose();
                } catch (SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }
        });
        dialog.setSize(250, 150);
        dialog.setVisible(true);
    }
    private void logout() {
        // Dispose of the current frame
        frame.dispose();

        // Open the Login GUI
        LoginGUI loginGUI = new LoginGUI(new LoginCallback() {
            @Override
            public void onLoginComplete(int userId) {
                // Handle login completion if needed
                System.out.println("User logged in with ID: " + userId);
            }
        });

        // Make the LoginGUI visible
        loginGUI.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinanceManagerGUI::new);
    }
    
}
