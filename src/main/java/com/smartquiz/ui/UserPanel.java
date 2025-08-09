package com.smartquiz.ui;

import com.smartquiz.database.DatabaseManager;
import com.smartquiz.models.QuizResult;
import com.smartquiz.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * User Panel for taking quizzes and viewing results
 */
public class UserPanel extends JFrame {
    private User currentUser;
    private DatabaseManager dbManager;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;

    public UserPanel(User user) {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        
        initializeComponents();
        setupLayout();
        loadUserResults();
        updateStats();
        
        setTitle("SmartQuiz - User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Results table
        String[] columnNames = {"Date", "Category", "Score", "Total", "Percentage", "Grade", "Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(37, 99, 235));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("User Dashboard - Welcome " + currentUser.getUsername());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(239, 68, 68));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(new Color(243, 244, 246));
        statsPanel.add(statsLabel);

        // Main Content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);

        // Left Panel - Quiz Options
        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));
        quizPanel.setBorder(BorderFactory.createTitledBorder("Take a Quiz"));
        quizPanel.setPreferredSize(new Dimension(280, 0));

        // Quiz configuration
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>();
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        categoryCombo.addItem("All Categories");

        JLabel difficultyLabel = new JLabel("Difficulty:");
        JComboBox<String> difficultyCombo = new JComboBox<>(new String[]{"All Levels", "easy", "medium", "hard"});
        difficultyCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel questionsLabel = new JLabel("Number of Questions:");
        JSpinner questionsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        questionsSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JButton startQuizButton = new JButton("Start Quiz");
        startQuizButton.setBackground(new Color(34, 197, 94));
        startQuizButton.setForeground(Color.WHITE);
        startQuizButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        startQuizButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startQuizButton.setFocusPainted(false);

        JButton refreshButton = new JButton("Refresh Results");
        refreshButton.setBackground(new Color(59, 130, 246));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.setFocusPainted(false);

        // Load categories
        try {
            List<String> categories = dbManager.getCategories();
            for (String category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }

        // Add components
        quizPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        quizPanel.add(categoryLabel);
        quizPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        quizPanel.add(categoryCombo);
        quizPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        quizPanel.add(difficultyLabel);
        quizPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        quizPanel.add(difficultyCombo);
        quizPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        quizPanel.add(questionsLabel);
        quizPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        quizPanel.add(questionsSpinner);
        quizPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        quizPanel.add(startQuizButton);
        quizPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        quizPanel.add(refreshButton);
        quizPanel.add(Box.createVerticalGlue());

        // Right Panel - Results History
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Quiz History"));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        historyPanel.add(statsPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(quizPanel);
        splitPane.setRightComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Button events
        startQuizButton.addActionListener(e -> {
            String category = categoryCombo.getSelectedItem().toString();
            if ("All Categories".equals(category)) category = null;
            
            String difficulty = difficultyCombo.getSelectedItem().toString();
            if ("All Levels".equals(difficulty)) difficulty = null;
            
            int numQuestions = (Integer) questionsSpinner.getValue();
            
            startQuiz(category, difficulty, numQuestions);
        });

        refreshButton.addActionListener(e -> {
            loadUserResults();
            updateStats();
        });
    }

    private void loadUserResults() {
        try {
            List<QuizResult> results = dbManager.getUserResults(currentUser.getId());
            tableModel.setRowCount(0);
            
            for (QuizResult result : results) {
                Object[] row = {
                    result.getCompletedAt(),
                    result.getCategory(),
                    result.getScore(),
                    result.getTotalQuestions(),
                    String.format("%.1f%%", result.getPercentage()),
                    result.getGrade(),
                    result.getTimeSpent() + "s"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStats() {
        try {
            List<QuizResult> results = dbManager.getUserResults(currentUser.getId());
            if (results.isEmpty()) {
                statsLabel.setText("No quizzes taken yet. Start your first quiz!");
                return;
            }

            int totalQuizzes = results.size();
            double avgScore = results.stream()
                .mapToDouble(QuizResult::getPercentage)
                .average()
                .orElse(0.0);
            
            QuizResult bestResult = results.stream()
                .max((r1, r2) -> Double.compare(r1.getPercentage(), r2.getPercentage()))
                .orElse(null);

            statsLabel.setText(String.format(
                "Total Quizzes: %d | Average Score: %.1f%% | Best Score: %.1f%% (%s)",
                totalQuizzes, avgScore, 
                bestResult != null ? bestResult.getPercentage() : 0.0,
                bestResult != null ? bestResult.getCategory() : "N/A"
            ));
        } catch (SQLException e) {
            statsLabel.setText("Error loading statistics");
        }
    }

    private void startQuiz(String category, String difficulty, int numQuestions) {
        try {
            QuizFrame quizFrame = new QuizFrame(currentUser, category, difficulty, numQuestions);
            quizFrame.setQuizCompleteCallback(() -> {
                loadUserResults();
                updateStats();
            });
            quizFrame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error starting quiz: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}