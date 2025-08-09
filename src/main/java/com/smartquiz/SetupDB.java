package com.smartquiz;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// Run once to create tables (or the app will call it automatically)
public class SetupDB {
    public static void init() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT DEFAULT 'user')";
            stmt.execute(createUsers);

            String createQuestions = "CREATE TABLE IF NOT EXISTS questions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "category TEXT," +
                    "question_text TEXT NOT NULL," +
                    "option_a TEXT," +
                    "option_b TEXT," +
                    "option_c TEXT," +
                    "option_d TEXT," +
                    "correct_answer TEXT)";
            stmt.execute(createQuestions);

            String createResults = "CREATE TABLE IF NOT EXISTS results (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "score INTEGER," +
                    "quiz_date TEXT DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createResults);

            // Create a default admin if not exists
            String admin = "INSERT OR IGNORE INTO users(username, password, role) VALUES('admin','admin', 'admin')";
            stmt.execute(admin);

            System.out.println("DB initialized (quiz_app.db)"); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
    }
}
