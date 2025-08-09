package com.smartquiz.models;

/**
 * Question model class representing a quiz question
 */
public class Question {
    private int id;
    private String category;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctAnswer; // 0=A, 1=B, 2=C, 3=D
    private String difficulty; // "easy", "medium", "hard"
    private String createdAt;

    // Constructors
    public Question() {}

    public Question(String category, String questionText, String optionA, String optionB,
                   String optionC, String optionD, int correctAnswer, String difficulty) {
        this.category = category;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String[] getOptions() {
        return new String[]{optionA, optionB, optionC, optionD};
    }

    public String getCorrectOptionText() {
        String[] options = getOptions();
        if (correctAnswer >= 0 && correctAnswer < options.length) {
            return options[correctAnswer];
        }
        return "";
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", questionText='" + questionText + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", correctAnswer=" + correctAnswer +
                '}';
    }
}