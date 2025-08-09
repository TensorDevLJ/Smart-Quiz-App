package com.smartquiz;

import java.sql.*;

public class AuthService {

    public static int login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, role FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean register(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            return false;
        }
    }

    public static String getRole(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT role FROM users WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("role");
        } catch (SQLException e) { e.printStackTrace(); }
        return "user";
    }
}
