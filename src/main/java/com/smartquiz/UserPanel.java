package com.smartquiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserPanel extends JFrame {
    private int userId;
    private List<Question> questions;
    private int index = 0;
    private int score = 0;

    private JLabel qLabel;
    private JRadioButton ra, rb, rc, rd;
    private ButtonGroup group;
    private JButton nextBtn;

    public UserPanel(int userId) {
        this.userId = userId;
        setTitle("SmartQuiz - Take Quiz"); setSize(700,400); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        questions = QuizService.fetchRandomQuestions(5);
        initUI();
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available. Ask admin to add questions.");
            dispose();
        } else {
            loadQuestion();
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());
        qLabel = new JLabel("Q", SwingConstants.LEFT);
        qLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        add(qLabel, BorderLayout.NORTH);

        JPanel options = new JPanel(new GridLayout(4,1));
        ra = new JRadioButton(); rb = new JRadioButton(); rc = new JRadioButton(); rd = new JRadioButton();
        group = new ButtonGroup(); group.add(ra); group.add(rb); group.add(rc); group.add(rd);
        options.add(ra); options.add(rb); options.add(rc); options.add(rd);
        add(options, BorderLayout.CENTER);

        nextBtn = new JButton("Next"); add(nextBtn, BorderLayout.SOUTH);
        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAndNext();
            }
        });
    }

    private void loadQuestion() {
        Question q = questions.get(index);
        qLabel.setText("Q" + (index+1) + ": " + q.text);
        ra.setText("A. " + q.a); rb.setText("B. " + q.b); rc.setText("C. " + q.c); rd.setText("D. " + q.d);
        group.clearSelection();
        if (index == questions.size()-1) nextBtn.setText("Finish"); else nextBtn.setText("Next");
    }

    private void checkAndNext() {
        Question q = questions.get(index);
        String sel = null;
        if (ra.isSelected()) sel = "A";
        if (rb.isSelected()) sel = "B";
        if (rc.isSelected()) sel = "C";
        if (rd.isSelected()) sel = "D";
        if (sel != null && sel.equalsIgnoreCase(q.correct)) score++;
        index++;
        if (index >= questions.size()) {
            QuizService.saveResult(userId, score);
            JOptionPane.showMessageDialog(this, "Quiz finished! Your score: " + score + " / " + questions.size());
            dispose();
        } else {
            loadQuestion();
        }
    }
}
