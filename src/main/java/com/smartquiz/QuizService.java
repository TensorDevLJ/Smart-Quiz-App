package com.smartquiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService {

    public static List<Question> fetchRandomQuestions(int limit) {
        List<Question> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM questions ORDER BY RANDOM() LIMIT ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question q = new Question(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer")
                );
                list.add(q);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void saveResult(int userId, int score) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO results(user_id, score) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, score);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
