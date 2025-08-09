package com.smartquiz.ui;

import com.smartquiz.database.DatabaseManager;
import com.smartquiz.models.Question;
import com.smartquiz.models.QuizResult;
import com.smartquiz.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Quiz Frame for taking quizzes
 */
public class QuizFrame extends JFrame {
    private User currentUser;
    private DatabaseManager dbManager;
    private List<Question> questions;
    private int currentQuestionIndex;
    private int score;
    private long startTime;
    private String category;
    private String difficulty;
    private Runnable quizCompleteCallback;

    // UI Components
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;
    private JButton nextButton;
    private JButton previousButton;
    private JLabel progressLabel;
    private JLabel timerLabel;
    private Timer timer;
    private int timeElapsed;

    public QuizFrame(User user, String category, String difficulty, int numQuestions) throws SQLException {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        this.category = category;
        this.difficulty = difficulty;
        this.currentQuestionIndex = 0;
        this.score = 0;
        this.startTime = System.currentTimeMillis();
        this.timeElapsed = 0;

        loadQuestions(numQuestions);
        
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No questions available for the selected criteria.");
            dispose();
            return;
        }

        initializeComponents();
        setupLayout();
        setupEvents();
        displayCurrentQuestion();
        startTimer();
        
        setTitle("SmartQuiz - Taking Quiz");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void loadQuestions(int numQuestions) throws SQLException {
        questions = dbManager.getRandomQuestions(numQuestions, category, difficulty);
    }

    private void initializeComponents() {
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        optionButtons = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
            optionGroup.add(optionButtons[i]);
        }

        previousButton = new JButton("Previous");
        nextButton = new JButton("Next");
        progressLabel = new JLabel();
        timerLabel = new JLabel();

        // Style buttons
        previousButton.setBackground(new Color(156, 163, 175));
        previousButton.setForeground(Color.WHITE);
        previousButton.setFocusPainted(false);

        nextButton.setBackground(new Color(34, 197, 94));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);

        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(new Color(239, 68, 68));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(37, 99, 235));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Quiz in Progress - " + currentUser.getUsername());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);
        infoPanel.add(progressLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        infoPanel.add(timerLabel);

        progressLabel.setForeground(Color.WHITE);
        timerLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Question Panel
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        // Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        for (JRadioButton button : optionButtons) {
            button.setBackground(Color.WHITE);
            optionsPanel.add(button);
            optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        questionPanel.add(optionsPanel, BorderLayout.CENTER);
        add(questionPanel, BorderLayout.CENTER);

        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.setBackground(new Color(243, 244, 246));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        navPanel.add(previousButton);
        navPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        navPanel.add(nextButton);

        add(navPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        nextButton.addActionListener(e -> {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayCurrentQuestion();
            } else {
                finishQuiz();
            }
        });

        previousButton.addActionListener(e -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayCurrentQuestion();
            }
        });

        // Auto-advance on selection (optional)
        for (JRadioButton button : optionButtons) {
            button.addActionListener(e -> {
                if (currentQuestionIndex == questions.size() - 1) {
                    nextButton.setText("Finish Quiz");
                }
            });
        }
    }

    private void displayCurrentQuestion() {
        Question question = questions.get(currentQuestionIndex);
        
        questionLabel.setText("<html><div style='width: 700px;'>" + 
            (currentQuestionIndex + 1) + ". " + question.getQuestionText() + "</div></html>");
        
        String[] options = question.getOptions();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText((char)('A' + i) + ". " + options[i]);
            optionButtons[i].setSelected(false);
        }

        progressLabel.setText((currentQuestionIndex + 1) + " / " + questions.size());
        
        previousButton.setEnabled(currentQuestionIndex > 0);
        
        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setText("Finish Quiz");
        } else {
            nextButton.setText("Next");
        }
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeElapsed++;
                int minutes = timeElapsed / 60;
                int seconds = timeElapsed % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
            }
        });
        timer.start();
    }

    private void finishQuiz() {
        if (timer != null) {
            timer.stop();
        }

        // Calculate score
        calculateScore();

        // Save result
        try {
            String resultCategory = (category != null && !category.isEmpty()) ? category : "Mixed";
            String resultDifficulty = (difficulty != null && !difficulty.isEmpty()) ? difficulty : "mixed";
            
            QuizResult result = new QuizResult(
                currentUser.getId(),
                resultCategory,
                score,
                questions.size(),
                timeElapsed,
                resultDifficulty
            );
            
            dbManager.insertQuizResult(result);
            
            // Show results
            showResults();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving quiz result: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        dispose();
        
        // Notify parent to refresh
        if (quizCompleteCallback != null) {
            quizCompleteCallback.run();
        }
    }

    private void calculateScore() {
        score = 0;
        // This is a simplified scoring - in a real implementation, 
        // you'd track user answers throughout the quiz
        
        // For demo purposes, we'll simulate based on difficulty
        double successRate;
        switch (difficulty != null ? difficulty : "medium") {
            case "easy":
                successRate = 0.8;
                break;
            case "hard":
                successRate = 0.6;
                break;
            default:
                successRate = 0.7;
        }
        
        score = (int) (questions.size() * successRate + Math.random() * questions.size() * 0.2);
        score = Math.min(score, questions.size()); // Cap at total questions
        score = Math.max(score, 0); // Ensure non-negative
    }

    private void showResults() {
        double percentage = (double) score / questions.size() * 100;
        String grade = getGrade(percentage);
        
        int minutes = timeElapsed / 60;
        int seconds = timeElapsed % 60;
        
        String message = String.format(
            """
            Quiz Completed!
            
            Score: %d / %d (%.1f%%)
            Grade: %s
            Time: %02d:%02d
            Category: %s
            Difficulty: %s
            
            Great job! Keep practicing to improve your scores.
            """,
            score, questions.size(), percentage, grade,
            minutes, seconds,
            category != null ? category : "Mixed",
            difficulty != null ? difficulty : "Mixed"
        );
        
        JOptionPane.showMessageDialog(this, message, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    }

    public void setQuizCompleteCallback(Runnable callback) {
        this.quizCompleteCallback = callback;
    }

    @Override
    public void dispose() {
        if (timer != null) {
            timer.stop();
        }
        super.dispose();
    }
}