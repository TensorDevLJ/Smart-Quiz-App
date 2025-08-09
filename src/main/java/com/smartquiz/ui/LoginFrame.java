package com.smartquiz.ui;

import com.smartquiz.database.DatabaseManager;
import com.smartquiz.models.User;
import com.smartquiz.utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Login Frame for user authentication
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private DatabaseManager dbManager;

    public LoginFrame() {
        this.dbManager = DatabaseManager.getInstance();
        initializeComponents();
        setupLayout();
        setupEvents();
        
        setTitle("SmartQuiz - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Style components
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        registerButton.setBackground(new Color(34, 197, 94));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(37, 99, 235));
        JLabel titleLabel = new JLabel("SmartQuiz System", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(243, 244, 246));
        JLabel infoLabel = new JLabel("<html><center>Default Admin: admin / admin123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        };

        loginButton.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);

        registerButton.addActionListener(e -> showRegistrationDialog());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", 
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = dbManager.authenticateUser(username, password);
            if (user != null) {
                // Login successful
                dispose();
                if (user.isAdmin()) {
                    new AdminPanel(user).setVisible(true);
                } else {
                    new UserPanel(user).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationDialog() {
        JDialog registerDialog = new JDialog(this, "Register New User", true);
        registerDialog.setSize(350, 400);
        registerDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField regUsernameField = new JTextField(20);
        JTextField regEmailField = new JTextField(20);
        JPasswordField regPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        // Add fields
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(regUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(regEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(regPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create Account");
        JButton cancelButton = new JButton("Cancel");

        createButton.setBackground(new Color(34, 197, 94));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);

        createButton.addActionListener(e -> {
            String username = regUsernameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "Please fill in all fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog, "Passwords do not match.");
                return;
            }

            if (!PasswordUtils.isValidPassword(password)) {
                JOptionPane.showMessageDialog(registerDialog, 
                    "Password must be at least 6 characters and contain both letters and numbers.");
                return;
            }

            try {
                if (dbManager.userExists(username)) {
                    JOptionPane.showMessageDialog(registerDialog, "Username already exists.");
                    return;
                }

                String hashedPassword = PasswordUtils.hashPassword(password);
                dbManager.insertUser(username, email, hashedPassword, "user");
                JOptionPane.showMessageDialog(registerDialog, "Account created successfully!");
                registerDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(registerDialog, "Error creating account: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> registerDialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(buttonPanel, gbc);

        registerDialog.add(panel);
        registerDialog.setVisible(true);
    }
}