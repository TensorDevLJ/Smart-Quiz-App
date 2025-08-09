package com.smartquiz.database;

import com.smartquiz.models.Question;
import com.smartquiz.models.QuizResult;
import com.smartquiz.models.User;
import com.smartquiz.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Database Manager class handling all database operations
 * Implements Singleton pattern for database connection management
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:sqlite:quiz_app.db";
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initialize database tables and default data
     */
    public void initializeDatabase() throws SQLException {
        createTables();
        insertDefaultData();
    }

    private void createTables() throws SQLException {
        // Create users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'user',
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

        // Create questions table
        String createQuestionsTable = """
            CREATE TABLE IF NOT EXISTS questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category TEXT NOT NULL,
                question_text TEXT NOT NULL,
                option_a TEXT NOT NULL,
                option_b TEXT NOT NULL,
                option_c TEXT NOT NULL,
                option_d TEXT NOT NULL,
                correct_answer INTEGER NOT NULL,
                difficulty TEXT NOT NULL DEFAULT 'medium',
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

        // Create quiz_results table
        String createResultsTable = """
            CREATE TABLE IF NOT EXISTS quiz_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category TEXT NOT NULL,
                score INTEGER NOT NULL,
                total_questions INTEGER NOT NULL,
                time_spent INTEGER NOT NULL,
                difficulty TEXT NOT NULL,
                completed_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users (id)
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createQuestionsTable);
            stmt.execute(createResultsTable);
            System.out.println("Database tables created successfully!");
        }
    }

    private void insertDefaultData() throws SQLException {
        // Insert default admin user if not exists
        if (!userExists("admin")) {
            String hashedPassword = PasswordUtils.hashPassword("admin123");
            insertUser("admin", "admin@smartquiz.com", hashedPassword, "admin");
            System.out.println("Default admin user created!");
        }

        // Insert sample questions if questions table is empty
        if (getQuestionCount() == 0) {
            insertSampleQuestions();
            System.out.println("Sample questions inserted!");
        }
    }

    private void insertSampleQuestions() throws SQLException {
        List<Question> sampleQuestions = List.of(
            new Question("Geography", "What is the capital of France?",
                "London", "Berlin", "Paris", "Madrid", 2, "easy"),
            new Question("Science", "What is the chemical symbol for gold?",
                "Go", "Gd", "Au", "Ag", 2, "medium"),
            new Question("Technology", "Which programming language is known for machine learning?",
                "JavaScript", "Python", "C++", "PHP", 1, "medium"),
            new Question("History", "In which year did World War II end?",
                "1944", "1945", "1946", "1947", 1, "easy"),
            new Question("Mathematics", "What is the value of π (pi) to 2 decimal places?",
                "3.14", "3.15", "3.16", "3.13", 0, "easy"),
            new Question("Literature", "Who wrote 'Romeo and Juliet'?",
                "Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain", 1, "medium"),
            new Question("Science", "What is the hardest natural substance on Earth?",
                "Gold", "Iron", "Diamond", "Platinum", 2, "medium"),
            new Question("Geography", "Which is the largest ocean on Earth?",
                "Atlantic", "Indian", "Arctic", "Pacific", 3, "easy"),
            new Question("Technology", "What does 'HTTP' stand for?",
                "HyperText Transfer Protocol", "High Tech Transfer Protocol",
                "Home Tool Transfer Protocol", "Hyperlink Text Transfer Protocol", 0, "hard"),
            new Question("Mathematics", "What is the square root of 144?",
                "11", "12", "13", "14", 1, "easy")
        );

        for (Question question : sampleQuestions) {
            insertQuestion(question);
        }
    }

    // User operations
    public boolean userExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }

    public void insertUser(String username, String email, String passwordHash, String role) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, passwordHash);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
        }
    }

    public User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (PasswordUtils.verifyPassword(password, storedHash)) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("created_at")
                    );
                }
            }
        }
        return null;
    }

    // Question operations
    public void insertQuestion(Question question) throws SQLException {
        String sql = """
            INSERT INTO questions (category, question_text, option_a, option_b, option_c, option_d, correct_answer, difficulty)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, question.getCategory());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getOptionA());
            pstmt.setString(4, question.getOptionB());
            pstmt.setString(5, question.getOptionC());
            pstmt.setString(6, question.getOptionD());
            pstmt.setInt(7, question.getCorrectAnswer());
            pstmt.setString(8, question.getDifficulty());
            pstmt.executeUpdate();
        }
    }

    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY category, created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setCategory(rs.getString("category"));
                question.setQuestionText(rs.getString("question_text"));
                question.setOptionA(rs.getString("option_a"));
                question.setOptionB(rs.getString("option_b"));
                question.setOptionC(rs.getString("option_c"));
                question.setOptionD(rs.getString("option_d"));
                question.setCorrectAnswer(rs.getInt("correct_answer"));
                question.setDifficulty(rs.getString("difficulty"));
                question.setCreatedAt(rs.getString("created_at"));
                questions.add(question);
            }
        }
        return questions;
    }

    public List<Question> getQuestionsByCategory(String category) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setCategory(rs.getString("category"));
                question.setQuestionText(rs.getString("question_text"));
                question.setOptionA(rs.getString("option_a"));
                question.setOptionB(rs.getString("option_b"));
                question.setOptionC(rs.getString("option_c"));
                question.setOptionD(rs.getString("option_d"));
                question.setCorrectAnswer(rs.getInt("correct_answer"));
                question.setDifficulty(rs.getString("difficulty"));
                question.setCreatedAt(rs.getString("created_at"));
                questions.add(question);
            }
        }
        return questions;
    }

    public List<Question> getRandomQuestions(int count, String category, String difficulty) throws SQLException {
        List<Question> allQuestions;

        if (category != null && !category.isEmpty() && difficulty != null && !difficulty.isEmpty()) {
            allQuestions = getQuestionsByCategoryAndDifficulty(category, difficulty);
        } else if (category != null && !category.isEmpty()) {
            allQuestions = getQuestionsByCategory(category);
        } else if (difficulty != null && !difficulty.isEmpty()) {
            allQuestions = getQuestionsByDifficulty(difficulty);
        } else {
            allQuestions = getAllQuestions();
        }
        
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(count, allQuestions.size()));
    }

    public List<Question> getQuestionsByDifficulty(String difficulty) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE difficulty = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, difficulty);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setCategory(rs.getString("category"));
                question.setQuestionText(rs.getString("question_text"));
                question.setOptionA(rs.getString("option_a"));
                question.setOptionB(rs.getString("option_b"));
                question.setOptionC(rs.getString("option_c"));
                question.setOptionD(rs.getString("option_d"));
                question.setCorrectAnswer(rs.getInt("correct_answer"));
                question.setDifficulty(rs.getString("difficulty"));
                question.setCreatedAt(rs.getString("created_at"));
                questions.add(question);
            }
        }
        return questions;
    }

    public List<Question> getQuestionsByCategoryAndDifficulty(String category, String difficulty) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category = ? AND difficulty = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setString(2, difficulty);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setCategory(rs.getString("category"));
                question.setQuestionText(rs.getString("question_text"));
                question.setOptionA(rs.getString("option_a"));
                question.setOptionB(rs.getString("option_b"));
                question.setOptionC(rs.getString("option_c"));
                question.setOptionD(rs.getString("option_d"));
                question.setCorrectAnswer(rs.getInt("correct_answer"));
                question.setDifficulty(rs.getString("difficulty"));
                question.setCreatedAt(rs.getString("created_at"));
                questions.add(question);
            }
        }
        return questions;
    }

    public void deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            pstmt.executeUpdate();
        }
    }

    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM questions ORDER BY category";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }

    public int getQuestionCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        }
    }

    // Quiz result operations
    public void insertQuizResult(QuizResult result) throws SQLException {
        String sql = """
            INSERT INTO quiz_results (user_id, category, score, total_questions, time_spent, difficulty)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, result.getUserId());
            pstmt.setString(2, result.getCategory());
            pstmt.setInt(3, result.getScore());
            pstmt.setInt(4, result.getTotalQuestions());
            pstmt.setInt(5, result.getTimeSpent());
            pstmt.setString(6, result.getDifficulty());
            pstmt.executeUpdate();
        }
    }

    public List<QuizResult> getUserResults(int userId) throws SQLException {
        List<QuizResult> results = new ArrayList<>();
        String sql = "SELECT * FROM quiz_results WHERE user_id = ? ORDER BY completed_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                QuizResult result = new QuizResult();
                result.setId(rs.getInt("id"));
                result.setUserId(rs.getInt("user_id"));
                result.setCategory(rs.getString("category"));
                result.setScore(rs.getInt("score"));
                result.setTotalQuestions(rs.getInt("total_questions"));
                result.setTimeSpent(rs.getInt("time_spent"));
                result.setDifficulty(rs.getString("difficulty"));
                result.setCompletedAt(rs.getString("completed_at"));
                results.add(result);
            }
        }
        return results;
    }

    public List<QuizResult> getTopScores(String category, int limit) throws SQLException {
        List<QuizResult> results = new ArrayList<>();
        String sql = """
            SELECT qr.*, u.username
            FROM quiz_results qr
            JOIN users u ON qr.user_id = u.id
            WHERE qr.category = ?
            ORDER BY (CAST(qr.score AS REAL) / qr.total_questions) DESC, qr.time_spent ASC
            LIMIT ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                QuizResult result = new QuizResult();
                result.setId(rs.getInt("id"));
                result.setUserId(rs.getInt("user_id"));
                result.setCategory(rs.getString("category"));
                result.setScore(rs.getInt("score"));
                result.setTotalQuestions(rs.getInt("total_questions"));
                result.setTimeSpent(rs.getInt("time_spent"));
                result.setDifficulty(rs.getString("difficulty"));
                result.setCompletedAt(rs.getString("completed_at"));
                results.add(result);
            }
        }
        return results;
    }

    // Utility methods
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}