package com.smartquiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminPanel extends JFrame {
    private JTextField categoryField, aField, bField, cField, dField, answerField;
    private JTextArea questionArea;
    public AdminPanel() {
        setTitle("SmartQuiz - Admin"); setSize(600,400); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel p = new JPanel(new BorderLayout());
        questionArea = new JTextArea(4, 40);
        p.add(new JScrollPane(questionArea), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6,2,5,5));
        form.add(new JLabel("Category:")); categoryField = new JTextField(); form.add(categoryField);
        form.add(new JLabel("Option A:")); aField = new JTextField(); form.add(aField);
        form.add(new JLabel("Option B:")); bField = new JTextField(); form.add(bField);
        form.add(new JLabel("Option C:")); cField = new JTextField(); form.add(cField);
        form.add(new JLabel("Option D:")); dField = new JTextField(); form.add(dField);
        form.add(new JLabel("Correct (A/B/C/D):")); answerField = new JTextField(); form.add(answerField);

        p.add(form, BorderLayout.CENTER);
        JButton addBtn = new JButton("Add Question"); p.add(addBtn, BorderLayout.SOUTH);
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addQuestion();
            }
        });

        add(p);
    }

    private void addQuestion() {
        String cat = categoryField.getText().trim();
        String q = questionArea.getText().trim();
        String a = aField.getText().trim();
        String b = bField.getText().trim();
        String c = cField.getText().trim();
        String d = dField.getText().trim();
        String ans = answerField.getText().trim().toUpperCase();
        if (q.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || !(ans.matches("[ABCD]"))) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and set correct answer as A/B/C/D");
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO questions(category, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cat);
            ps.setString(2, q);
            ps.setString(3, a);
            ps.setString(4, b);
            ps.setString(5, c);
            ps.setString(6, d);
            ps.setString(7, ans);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Question added!");
            questionArea.setText(""); aField.setText(""); bField.setText(""); cField.setText(""); dField.setText(""); answerField.setText(""); categoryField.setText(""); 
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error adding question"); }
    }
}
