package com.mockapi.server.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LogPanel extends JPanel {

    private static final String UI_FONT = UIHelper.getSystemUIFont();
    private static final String MONO_FONT = UIHelper.getSystemMonoFont();

    private JTextArea logArea;

    public LogPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Activity Log", TitledBorder.LEFT, TitledBorder.TOP,
                new Font(UI_FONT, Font.BOLD, 12), new Color(63, 81, 181)),
            new EmptyBorder(5, 5, 5, 5)
        ));

        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font(MONO_FONT, Font.PLAIN, 11));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setText("Ready to create mock endpoints...\n");

        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void appendLog(String message) {
        logArea.append(message);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public JTextArea getLogArea() {
        return logArea;
    }
}

