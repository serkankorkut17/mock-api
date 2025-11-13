package com.mockapi.server.ui;

import com.mockapi.server.service.MockService;
import com.mockapi.server.service.RabbitMQService;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

@Component
@Profile("ui")
public class MockApiFrame extends JFrame {

    private final MockService mockService;
    private final RabbitMQService rabbitMQService;
    private JTextArea logArea;

    public MockApiFrame(MockService mockService, RabbitMQService rabbitMQService) {
        this.mockService = mockService;
        this.rabbitMQService = rabbitMQService;

        // Window configuration
        setTitle("🚀 Mock API Manager");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 250));

        // Title panel
        JPanel titlePanel = createTitlePanel();

        // Content panel with two sections
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setOpaque(false);

        // Form panel (Mock API)
        JPanel formPanel = createFormPanel();

        // RabbitMQ panel
        JPanel rabbitPanel = createRabbitMQPanel();

        contentPanel.add(formPanel);
        contentPanel.add(rabbitPanel);

        // Log panel
        JPanel logPanel = createLogPanel();

        // Add panels to main
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(63, 81, 181));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Mock API Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Create dynamic mock endpoints");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 200, 255));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Mock Configuration", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(63, 81, 181)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Path field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel pathLabel = new JLabel("Endpoint Path:");
        pathLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(pathLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JTextField pathField = new JTextField("/api/test");
        pathField.setFont(new Font("Consolas", Font.PLAIN, 12));
        pathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        fieldsPanel.add(pathField, gbc);

        // Method field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel methodLabel = new JLabel("HTTP Method:");
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(methodLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<String> methodBox = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE", "PATCH"});
        methodBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldsPanel.add(methodBox, gbc);

        // Status Code field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel statusLabel = new JLabel("Status Code:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(statusLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{
            "200 - OK",
            "201 - Created",
            "204 - No Content",
            "400 - Bad Request",
            "401 - Unauthorized",
            "403 - Forbidden",
            "404 - Not Found",
            "500 - Internal Server Error"
        });
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(statusBox);
        fieldsPanel.add(statusPanel, gbc);

        // Response JSON field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel jsonLabel = new JLabel("Response JSON:");
        jsonLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(jsonLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        JTextArea jsonArea = new JTextArea(8, 30);
        jsonArea.setText("{\n  \"status\": \"success\",\n  \"message\": \"Hello from Mock API\",\n  \"timestamp\": \"2025-11-13T10:00:00Z\"\n}");
        jsonArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        jsonArea.setLineWrap(true);
        jsonArea.setWrapStyleWord(true);
        jsonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        JScrollPane jsonScroll = new JScrollPane(jsonArea);
        fieldsPanel.add(jsonScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearButton.setBackground(new Color(240, 240, 240));
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 20, 8, 20)
        ));
        clearButton.addActionListener(e -> {
            pathField.setText("/api/");
            methodBox.setSelectedIndex(0);
            statusBox.setSelectedIndex(0);
            jsonArea.setText("{\n  \"message\": \"Response\"\n}");
        });

        JButton addButton = new JButton("✓ Add Mock");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 142, 60)),
            new EmptyBorder(8, 25, 8, 25)
        ));
        addButton.addActionListener(e -> {
            String path = pathField.getText().trim();
            String method = (String) methodBox.getSelectedItem();
            String statusText = (String) statusBox.getSelectedItem();
            int statusCode = Integer.parseInt(statusText.split(" ")[0]);
            String json = jsonArea.getText().trim();

            if (path.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Path cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            mockService.addMock(path, method, statusCode, json);

            String logMessage = String.format("✓ Mock added: %s %s → %d\n", method, path, statusCode);
            logArea.append(logMessage);
            logArea.setCaretPosition(logArea.getDocument().getLength());

            JOptionPane.showMessageDialog(this,
                String.format("Mock endpoint created!\n\nEndpoint: %s\nMethod: %s\nStatus: %d", path, method, statusCode),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);

        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRabbitMQPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "RabbitMQ Messaging", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(255, 87, 34)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Declare text fields at method scope for listener access
        final JTextField queueField;
        final JTextField vhostField;
        final JTextField exchangeField;
        final JTextField routingField;
        final JComboBox<String> contentTypeBox;
        final JTextArea rabbitMessageArea;

        // Fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Queue Name field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel queueLabel = new JLabel("Queue Name:");
        queueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(queueLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        queueField = new JTextField("test-queue");
        queueField.setFont(new Font("Consolas", Font.PLAIN, 12));
        queueField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        fieldsPanel.add(queueField, gbc);

        // Virtual Host field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel vhostLabel = new JLabel("Virtual Host:");
        vhostLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(vhostLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        vhostField = new JTextField("cert");
        vhostField.setFont(new Font("Consolas", Font.PLAIN, 12));
        vhostField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        vhostField.setToolTipText("RabbitMQ Virtual Host (default: cert)");
        fieldsPanel.add(vhostField, gbc);

        // Exchange field (optional)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel exchangeLabel = new JLabel("Exchange (Optional):");
        exchangeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(exchangeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        exchangeField = new JTextField("");
        exchangeField.setFont(new Font("Consolas", Font.PLAIN, 12));
        exchangeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        fieldsPanel.add(exchangeField, gbc);

        // Routing Key field (optional)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel routingLabel = new JLabel("Routing Key:");
        routingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(routingLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        routingField = new JTextField("");
        routingField.setFont(new Font("Consolas", Font.PLAIN, 12));
        routingField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        fieldsPanel.add(routingField, gbc);

        // Content-Type field
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel contentTypeLabel = new JLabel("Content-Type:");
        contentTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(contentTypeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentTypeBox = new JComboBox<>(new String[]{"application/json", "text/plain"});
        contentTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldsPanel.add(contentTypeBox, gbc);

        // Message field
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fieldsPanel.add(messageLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        rabbitMessageArea = new JTextArea(8, 30);
        rabbitMessageArea.setText("{\n  \"event\": \"test\",\n  \"data\": \"Hello RabbitMQ\"\n}");
        rabbitMessageArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        rabbitMessageArea.setLineWrap(true);
        rabbitMessageArea.setWrapStyleWord(true);
        rabbitMessageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        JScrollPane messageScroll = new JScrollPane(rabbitMessageArea);
        fieldsPanel.add(messageScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearButton.setBackground(new Color(240, 240, 240));
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 20, 8, 20)
        ));
        clearButton.addActionListener(e -> {
            queueField.setText("test-queue");
            vhostField.setText("cert");
            exchangeField.setText("");
            routingField.setText("");
            rabbitMessageArea.setText("{\n  \"message\": \"Hello\"\n}");
        });

        JButton sendButton = new JButton("📤 Send Message");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sendButton.setBackground(new Color(255, 87, 34));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 74, 25)),
            new EmptyBorder(8, 25, 8, 25)
        ));
        sendButton.addActionListener(e -> {
            String queue = queueField.getText().trim();
            String vhost = vhostField.getText().trim();
            String exchange = exchangeField.getText().trim();
            String routingKey = routingField.getText().trim();
            String contentType = (String) contentTypeBox.getSelectedItem();
            String message = rabbitMessageArea.getText().trim();

            boolean asJson = "application/json".equals(contentType);

            if (queue.isEmpty() && exchange.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Queue name or Exchange must be provided!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Message cannot be empty!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (!exchange.isEmpty()) {
                    rabbitMQService.sendMessageToExchange(exchange, routingKey.isEmpty() ? "" : routingKey, message, vhost.isEmpty() ? null : vhost, asJson);
                    String logMessage = String.format("📤 Message sent to Exchange: %s (Routing: %s, VHost: %s, Type: %s)\n",
                        exchange, routingKey.isEmpty() ? "none" : routingKey, vhost.isEmpty() ? "default" : vhost, contentType);
                    logArea.append(logMessage);
                } else {
                    rabbitMQService.sendMessageToQueue(queue, message, vhost.isEmpty() ? null : vhost, asJson);
                    String logMessage = String.format("📤 Message sent to Queue: %s (VHost: %s, Type: %s)\n", queue, vhost.isEmpty() ? "default" : vhost, contentType);
                    logArea.append(logMessage);
                }

                logArea.setCaretPosition(logArea.getDocument().getLength());

                JOptionPane.showMessageDialog(this,
                    "Message sent successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                String errorLog = String.format("❌ Error: %s\n", ex.getMessage());
                logArea.append(errorLog);
                logArea.setCaretPosition(logArea.getDocument().getLength());

                JOptionPane.showMessageDialog(this,
                    "Failed to send message:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(clearButton);
        buttonPanel.add(sendButton);

        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Activity Log", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(63, 81, 181)),
            new EmptyBorder(5, 5, 5, 5)
        ));

        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setText("Ready to create mock endpoints...\n");

        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
