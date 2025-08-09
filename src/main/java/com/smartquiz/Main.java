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
