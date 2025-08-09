package com.smartquiz.models;

/**
 * QuizResult model class representing a completed quiz result
 */
public class QuizResult {
    private int id;
    private int userId;
    private String category;
    private int score;
    private int totalQuestions;
    private int timeSpent; // in seconds
    private String difficulty;
    private String completedAt;

    // Constructors
    public QuizResult() {}

    public QuizResult(int userId, String category, int score, int totalQuestions,
                     int timeSpent, String difficulty) {
        this.userId = userId;
        this.category = category;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeSpent = timeSpent;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public double getPercentage() {
        if (totalQuestions == 0) return 0.0;
        return (double) score / totalQuestions * 100.0;
    }

    public String getGrade() {
        double percentage = getPercentage();
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", userId=" + userId +
                ", category='" + category + '\'' +
                ", score=" + score +
                ", totalQuestions=" + totalQuestions +
                ", percentage=" + String.format("%.1f", getPercentage()) + "%" +
                ", grade='" + getGrade() + '\'' +
                ", completedAt='" + completedAt + '\'' +
                '}';
    }
}