package com.smartquiz.ui;

import com.smartquiz.database.DatabaseManager;
import com.smartquiz.models.Question;
import com.smartquiz.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin Panel for managing questions and viewing statistics
 */
public class AdminPanel extends JFrame {
    private User currentUser;
    private DatabaseManager dbManager;
    private JTable questionsTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;

    public AdminPanel(User user) {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        
        initializeComponents();
        setupLayout();
        setupEvents();
        loadQuestions();
        updateStats();
        
        setTitle("SmartQuiz - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Category", "Question", "Difficulty", "Correct Answer"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionsTable = new JTable(tableModel);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(300);

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

        JLabel titleLabel = new JLabel("Admin Dashboard - Welcome " + currentUser.getUsername());
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
        add(statsPanel, BorderLayout.SOUTH);

        // Main Content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);

        // Left Panel - Controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Question Management"));
        controlPanel.setPreferredSize(new Dimension(280, 0));

        JButton addQuestionBtn = new JButton("Add New Question");
        JButton editQuestionBtn = new JButton("Edit Selected");
        JButton deleteQuestionBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh List");

        // Style buttons
        addQuestionBtn.setBackground(new Color(34, 197, 94));
        addQuestionBtn.setForeground(Color.WHITE);
        addQuestionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        addQuestionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        editQuestionBtn.setBackground(new Color(251, 146, 60));
        editQuestionBtn.setForeground(Color.WHITE);
        editQuestionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        editQuestionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        deleteQuestionBtn.setBackground(new Color(239, 68, 68));
        deleteQuestionBtn.setForeground(Color.WHITE);
        deleteQuestionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        deleteQuestionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        refreshBtn.setBackground(new Color(59, 130, 246));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(addQuestionBtn);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(editQuestionBtn);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(deleteQuestionBtn);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(refreshBtn);
        controlPanel.add(Box.createVerticalGlue());

        // Right Panel - Questions Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Questions Database"));
        
        JScrollPane scrollPane = new JScrollPane(questionsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(controlPanel);
        splitPane.setRightComponent(tablePanel);
        add(splitPane, BorderLayout.CENTER);

        // Button events
        addQuestionBtn.addActionListener(e -> showAddQuestionDialog());
        editQuestionBtn.addActionListener(e -> editSelectedQuestion());
        deleteQuestionBtn.addActionListener(e -> deleteSelectedQuestion());
        refreshBtn.addActionListener(e -> {
            loadQuestions();
            updateStats();
        });
    }

    private void setupEvents() {
        // Double-click to edit
        questionsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editSelectedQuestion();
                }
            }
        });
    }

    private void loadQuestions() {
        try {
            List<Question> questions = dbManager.getAllQuestions();
            tableModel.setRowCount(0);
            
            for (Question question : questions) {
                Object[] row = {
                    question.getId(),
                    question.getCategory(),
                    question.getQuestionText().length() > 50 ? 
                        question.getQuestionText().substring(0, 50) + "..." : 
                        question.getQuestionText(),
                    question.getDifficulty(),
                    question.getCorrectOptionText()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStats() {
        try {
            int totalQuestions = dbManager.getQuestionCount();
            List<String> categories = dbManager.getCategories();
            statsLabel.setText("Total Questions: " + totalQuestions + " | Categories: " + categories.size());
        } catch (SQLException e) {
            statsLabel.setText("Error loading statistics");
        }
    }

    private void showAddQuestionDialog() {
        QuestionDialog dialog = new QuestionDialog(this, "Add New Question", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Question newQuestion = dialog.getQuestion();
            try {
                dbManager.insertQuestion(newQuestion);
                loadQuestions();
                updateStats();
                JOptionPane.showMessageDialog(this, "Question added successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding question: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to edit.");
            return;
        }

        try {
            int questionId = (Integer) tableModel.getValueAt(selectedRow, 0);
            List<Question> questions = dbManager.getAllQuestions();
            Question selectedQuestion = questions.stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .orElse(null);

            if (selectedQuestion != null) {
                QuestionDialog dialog = new QuestionDialog(this, "Edit Question", selectedQuestion);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    // Delete old and insert new (simple update approach)
                    dbManager.deleteQuestion(questionId);
                    dbManager.insertQuestion(dialog.getQuestion());
                    loadQuestions();
                    updateStats();
                    JOptionPane.showMessageDialog(this, "Question updated successfully!");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error editing question: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this question?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                int questionId = (Integer) tableModel.getValueAt(selectedRow, 0);
                dbManager.deleteQuestion(questionId);
                loadQuestions();
                updateStats();
                JOptionPane.showMessageDialog(this, "Question deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting question: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    // Inner class for Question Dialog
    private static class QuestionDialog extends JDialog {
        private JTextField categoryField;
        private JTextArea questionTextArea;
        private JTextField optionAField;
        private JTextField optionBField;
        private JTextField optionCField;
        private JTextField optionDField;
        private JComboBox<String> correctAnswerCombo;
        private JComboBox<String> difficultyCombo;
        private boolean confirmed = false;

        public QuestionDialog(Frame parent, String title, Question question) {
            super(parent, title, true);
            initializeComponents(question);
            setupLayout();
            setSize(500, 600);
            setLocationRelativeTo(parent);
        }

        private void initializeComponents(Question question) {
            categoryField = new JTextField(20);
            questionTextArea = new JTextArea(4, 30);
            questionTextArea.setLineWrap(true);
            questionTextArea.setWrapStyleWord(true);
            
            optionAField = new JTextField(30);
            optionBField = new JTextField(30);
            optionCField = new JTextField(30);
            optionDField = new JTextField(30);
            
            correctAnswerCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
            difficultyCombo = new JComboBox<>(new String[]{"easy", "medium", "hard"});

            // Populate if editing
            if (question != null) {
                categoryField.setText(question.getCategory());
                questionTextArea.setText(question.getQuestionText());
                optionAField.setText(question.getOptionA());
                optionBField.setText(question.getOptionB());
                optionCField.setText(question.getOptionC());
                optionDField.setText(question.getOptionD());
                correctAnswerCombo.setSelectedIndex(question.getCorrectAnswer());
                difficultyCombo.setSelectedItem(question.getDifficulty());
            }
        }

        private void setupLayout() {
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // Category
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Category:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(categoryField, gbc);

            // Question Text
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("Question:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
            formPanel.add(new JScrollPane(questionTextArea), gbc);

            // Options
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("Option A:"), gbc);
            gbc.gridx = 1;
            formPanel.add(optionAField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("Option B:"), gbc);
            gbc.gridx = 1;
            formPanel.add(optionBField, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            formPanel.add(new JLabel("Option C:"), gbc);
            gbc.gridx = 1;
            formPanel.add(optionCField, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            formPanel.add(new JLabel("Option D:"), gbc);
            gbc.gridx = 1;
            formPanel.add(optionDField, gbc);

            // Correct Answer
            gbc.gridx = 0; gbc.gridy = 6;
            formPanel.add(new JLabel("Correct Answer:"), gbc);
            gbc.gridx = 1;
            formPanel.add(correctAnswerCombo, gbc);

            // Difficulty
            gbc.gridx = 0; gbc.gridy = 7;
            formPanel.add(new JLabel("Difficulty:"), gbc);
            gbc.gridx = 1;
            formPanel.add(difficultyCombo, gbc);

            add(formPanel, BorderLayout.CENTER);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");

            saveButton.setBackground(new Color(34, 197, 94));
            saveButton.setForeground(Color.WHITE);
            saveButton.addActionListener(e -> {
                if (validateInput()) {
                    confirmed = true;
                    dispose();
                }
            });

            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private boolean validateInput() {
            if (categoryField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a category.");
                return false;
            }
            if (questionTextArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a question.");
                return false;
            }
            if (optionAField.getText().trim().isEmpty() || 
                optionBField.getText().trim().isEmpty() ||
                optionCField.getText().trim().isEmpty() || 
                optionDField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all options.");
                return false;
            }
            return true;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public Question getQuestion() {
            return new Question(
                categoryField.getText().trim(),
                questionTextArea.getText().trim(),
                optionAField.getText().trim(),
                optionBField.getText().trim(),
                optionCField.getText().trim(),
                optionDField.getText().trim(),
                correctAnswerCombo.getSelectedIndex(),
                (String) difficultyCombo.getSelectedItem()
            );
        }
    }
}