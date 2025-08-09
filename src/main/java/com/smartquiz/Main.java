package com.smartquiz;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    public Main() {
        SetupDB.init(); // ensure DB + tables exist
        setTitle("SmartQuiz"); setSize(400,300); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("SmartQuiz", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(5,1,5,5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        center.add(new JLabel("Username:")); center.add(usernameField);
        center.add(new JLabel("Password:")); center.add(passwordField);

        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        south.add(loginBtn); south.add(registerBtn);

        JButton adminBtn = new JButton("Open Admin Panel (admin/admin)"); south.add(adminBtn);

        add(south, BorderLayout.SOUTH);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = usernameField.getText().trim();
                String pass = new String(passwordField.getPassword()).trim();
                int id = AuthService.login(user, pass);
                if (id != -1) {
                    JOptionPane.showMessageDialog(Main.this, "Login success!");
                    String role = AuthService.getRole(id);
                    if ("admin".equalsIgnoreCase(role)) {
                        AdminPanel ap = new AdminPanel();
                        ap.setVisible(true);
                    } else {
                        UserPanel up = new UserPanel(id);
                        up.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(Main.this, "Invalid credentials");
                }
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = usernameField.getText().trim();
                String pass = new String(passwordField.getPassword()).trim();
                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(Main.this, "Enter username and password to register");
                    return;
                }
                boolean ok = AuthService.register(user, pass);
                if (ok) JOptionPane.showMessageDialog(Main.this, "Registered. Now login."); else JOptionPane.showMessageDialog(Main.this, "Registration failed (maybe username exists)"); 
            }
        });

        adminBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // quick admin login
                AdminPanel ap = new AdminPanel(); ap.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main m = new Main();
            m.setVisible(true);
        });
    }
}
=======
import com.smartquiz.database.DatabaseManager;
import com.smartquiz.ui.LoginFrame;

import javax.swing.*;

/**
 * Main class for SmartQuiz Application
 * Entry point that initializes the database and launches the login interface
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Set Look and Feel to system default for better appearance
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Initialize database on startup
        try {
            DatabaseManager.getInstance().initializeDatabase();
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database. Application will exit.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Launch the application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to launch application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

