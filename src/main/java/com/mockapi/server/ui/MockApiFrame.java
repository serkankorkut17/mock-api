package com.mockapi.server.ui;

import com.mockapi.server.service.MockService;
import com.mockapi.server.service.RabbitMQService;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mockapi.server.service.MockEndpoint;

@Component
@Profile("ui")
public class MockApiFrame extends JFrame {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // Platform-specific font names
    private static final String UI_FONT = getSystemUIFont();
    private static final String MONO_FONT = getSystemMonoFont();

    private final MockService mockService;
    private final RabbitMQService rabbitMQService;
    private JTextArea logArea;
    private JButton endpointsToggleButton;
    private JButton messagesToggleButton;

    /**
     * Get platform-specific UI font
     */
    private static String getSystemUIFont() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return "SF Pro Text"; // macOS system font
        } else if (os.contains("win")) {
            return "Segoe UI"; // Windows system font
        } else {
            return "SansSerif"; // Linux fallback
        }
    }

    /**
     * Get platform-specific monospace font
     */
    private static String getSystemMonoFont() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return "SF Mono"; // macOS monospace font
        } else if (os.contains("win")) {
            return "Consolas"; // Windows monospace font
        } else {
            return "Monospaced"; // Linux fallback
        }
    }

    public MockApiFrame(MockService mockService, RabbitMQService rabbitMQService) {
        this.mockService = mockService;
        this.rabbitMQService = rabbitMQService;

        // Window configuration
        setTitle("🚀 Mock API Manager");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Set application icon
        setApplicationIcon();

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
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Create dynamic mock endpoints");
        subtitleLabel.setFont(new Font(UI_FONT, Font.PLAIN, 12));
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
        pathLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(pathLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JTextField pathField = new JTextField("/api/test");
        pathField.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        pathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        addUndoRedoSupport(pathField);
        fieldsPanel.add(pathField, gbc);

        // Method field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel methodLabel = new JLabel("HTTP Method:");
        methodLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(methodLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<String> methodBox = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE", "PATCH"});
        methodBox.setFont(new Font(UI_FONT, Font.PLAIN, 12));
        fieldsPanel.add(methodBox, gbc);

        // Status Code field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel statusLabel = new JLabel("Status Code:");
        statusLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
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
        statusBox.setFont(new Font(UI_FONT, Font.PLAIN, 12));
        statusPanel.add(statusBox);
        fieldsPanel.add(statusPanel, gbc);

        // Response JSON field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel jsonLabel = new JLabel("Response JSON:");
        jsonLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(jsonLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;

        // Container with relative positioning for overlay button
        JPanel jsonContainer = new JPanel(new BorderLayout());
        jsonContainer.setBackground(Color.WHITE);

        JTextArea jsonArea = new JTextArea(8, 30);
        jsonArea.setText("{\n  \"status\": \"success\",\n  \"message\": \"Hello from Mock API\",\n  \"timestamp\": \"2025-11-13T10:00:00Z\"\n}");
        jsonArea.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        jsonArea.setLineWrap(true);
        jsonArea.setWrapStyleWord(true);
        jsonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        addUndoRedoSupport(jsonArea);
        JScrollPane jsonScroll = new JScrollPane(jsonArea);

        // Small prettify button overlay in bottom-right corner
        JButton prettifyButton = new JButton("✨");
        prettifyButton.setFont(new Font(UI_FONT, Font.PLAIN, 10));
        prettifyButton.setBackground(new Color(103, 58, 183));
        prettifyButton.setForeground(Color.WHITE);
        prettifyButton.setFocusPainted(false);
        prettifyButton.setToolTipText("Format JSON");
        prettifyButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(81, 45, 168)),
            new EmptyBorder(3, 8, 3, 8)
        ));
        prettifyButton.addActionListener(e -> {
            try {
                String formatted = prettifyJson(jsonArea.getText());
                jsonArea.setText(formatted);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "JSON Format Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Layered pane for overlay effect
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        jsonScroll.setAlignmentX(0.0f);
        jsonScroll.setAlignmentY(0.0f);
        layeredPane.add(jsonScroll, JLayeredPane.DEFAULT_LAYER);

        // Button panel positioned at bottom-right
        JPanel buttonOverlay = new JPanel(new BorderLayout());
        buttonOverlay.setOpaque(false);
        buttonOverlay.setAlignmentX(0.0f);
        buttonOverlay.setAlignmentY(0.0f);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(prettifyButton);

        buttonOverlay.add(buttonWrapper, BorderLayout.SOUTH);
        layeredPane.add(buttonOverlay, JLayeredPane.PALETTE_LAYER);

        jsonContainer.add(layeredPane, BorderLayout.CENTER);
        fieldsPanel.add(jsonContainer, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
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
        addButton.setFont(new Font(UI_FONT, Font.BOLD, 12));
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

            System.out.println("🔍 DEBUG: Mock added - " + method + " " + path + " (Status: " + statusCode + ")");
            System.out.println("🔍 DEBUG: Total endpoints now: " + mockService.getMocksList().size());

            String logMessage = String.format("✓ Mock added: %s %s → %d\n", method, path, statusCode);
            logArea.append(logMessage);
            logArea.setCaretPosition(logArea.getDocument().getLength());

            // Update the endpoints toggle button count
            updateEndpointsButton();

            JOptionPane.showMessageDialog(this,
                String.format("Mock endpoint created!\n\nEndpoint: %s\nMethod: %s\nStatus: %d", path, method, statusCode),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        // Bottom section with both buttons and endpoints list
        JPanel bottomSection = new JPanel(new BorderLayout(5, 5));
        bottomSection.setBackground(Color.WHITE);
        bottomSection.add(buttonPanel, BorderLayout.NORTH);
        bottomSection.add(createEndpointsListPanel(), BorderLayout.CENTER);

        panel.add(bottomSection, BorderLayout.SOUTH);

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
        queueLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(queueLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        queueField = new JTextField("test-queue");
        queueField.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        queueField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        addUndoRedoSupport(queueField);
        fieldsPanel.add(queueField, gbc);

        // Virtual Host field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel vhostLabel = new JLabel("Virtual Host:");
        vhostLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(vhostLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        vhostField = new JTextField("cert");
        vhostField.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        vhostField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        vhostField.setToolTipText("RabbitMQ Virtual Host (default: cert)");
        addUndoRedoSupport(vhostField);
        fieldsPanel.add(vhostField, gbc);

        // Exchange field (optional)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel exchangeLabel = new JLabel("Exchange (Optional):");
        exchangeLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(exchangeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        exchangeField = new JTextField("");
        exchangeField.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        exchangeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        addUndoRedoSupport(exchangeField);
        fieldsPanel.add(exchangeField, gbc);

        // Routing Key field (optional)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel routingLabel = new JLabel("Routing Key:");
        routingLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(routingLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        routingField = new JTextField("");
        routingField.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        routingField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        addUndoRedoSupport(routingField);
        fieldsPanel.add(routingField, gbc);

        // Content-Type field
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel contentTypeLabel = new JLabel("Content-Type:");
        contentTypeLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(contentTypeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentTypeBox = new JComboBox<>(new String[]{"application/json", "text/plain"});
        contentTypeBox.setFont(new Font(UI_FONT, Font.PLAIN, 12));
        fieldsPanel.add(contentTypeBox, gbc);

        // Message field
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(messageLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;

        // Container with relative positioning for overlay button
        JPanel messageContainer = new JPanel(new BorderLayout());
        messageContainer.setBackground(Color.WHITE);

        rabbitMessageArea = new JTextArea(8, 30);
        rabbitMessageArea.setText("{\n  \"event\": \"test\",\n  \"data\": \"Hello RabbitMQ\"\n}");
        rabbitMessageArea.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        rabbitMessageArea.setLineWrap(true);
        rabbitMessageArea.setWrapStyleWord(true);
        rabbitMessageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        addUndoRedoSupport(rabbitMessageArea);
        JScrollPane messageScroll = new JScrollPane(rabbitMessageArea);

        // Small prettify button overlay in bottom-right corner
        JButton rabbitPrettifyButton = new JButton("✨");
        rabbitPrettifyButton.setFont(new Font(UI_FONT, Font.PLAIN, 10));
        rabbitPrettifyButton.setBackground(new Color(103, 58, 183));
        rabbitPrettifyButton.setForeground(Color.WHITE);
        rabbitPrettifyButton.setFocusPainted(false);
        rabbitPrettifyButton.setToolTipText("Format JSON");
        rabbitPrettifyButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(81, 45, 168)),
            new EmptyBorder(3, 8, 3, 8)
        ));
        rabbitPrettifyButton.addActionListener(e -> {
            try {
                String formatted = prettifyJson(rabbitMessageArea.getText());
                rabbitMessageArea.setText(formatted);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "JSON Format Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Layered pane for overlay effect
        JLayeredPane rabbitLayeredPane = new JLayeredPane();
        rabbitLayeredPane.setLayout(new OverlayLayout(rabbitLayeredPane));

        messageScroll.setAlignmentX(0.0f);
        messageScroll.setAlignmentY(0.0f);
        rabbitLayeredPane.add(messageScroll, JLayeredPane.DEFAULT_LAYER);

        // Button panel positioned at bottom-right
        JPanel rabbitButtonOverlay = new JPanel(new BorderLayout());
        rabbitButtonOverlay.setOpaque(false);
        rabbitButtonOverlay.setAlignmentX(0.0f);
        rabbitButtonOverlay.setAlignmentY(0.0f);

        JPanel rabbitButtonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rabbitButtonWrapper.setOpaque(false);
        rabbitButtonWrapper.add(rabbitPrettifyButton);

        rabbitButtonOverlay.add(rabbitButtonWrapper, BorderLayout.SOUTH);
        rabbitLayeredPane.add(rabbitButtonOverlay, JLayeredPane.PALETTE_LAYER);

        messageContainer.add(rabbitLayeredPane, BorderLayout.CENTER);
        fieldsPanel.add(messageContainer, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font(UI_FONT, Font.PLAIN, 12));
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
        sendButton.setFont(new Font(UI_FONT, Font.BOLD, 12));
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

                // Update the messages toggle button count
                updateMessagesButton();

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

        // Bottom section with both buttons and messages list
        JPanel bottomSection = new JPanel(new BorderLayout(5, 5));
        bottomSection.setBackground(Color.WHITE);
        bottomSection.add(buttonPanel, BorderLayout.NORTH);
        bottomSection.add(createMessagesListPanel(), BorderLayout.CENTER);

        panel.add(bottomSection, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
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
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Adds undo/redo support to a text component with platform-specific shortcuts
     * Windows/Linux: Ctrl+Z (undo), Ctrl+Y (redo)
     * macOS: Cmd+Z (undo), Cmd+Shift+Z (redo)
     */
    private void addUndoRedoSupport(JTextArea textArea) {
        UndoManager undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);

        // Detect OS
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        int modifier = isMac ? KeyEvent.META_DOWN_MASK : KeyEvent.CTRL_DOWN_MASK;

        // Undo: Ctrl+Z (Windows/Linux) or Cmd+Z (macOS)
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier);
        textArea.getInputMap().put(undoKeyStroke, "Undo");
        textArea.getActionMap().put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });

        // Redo: Ctrl+Y (Windows/Linux) or Cmd+Shift+Z (macOS)
        KeyStroke redoKeyStroke;
        if (isMac) {
            redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier | KeyEvent.SHIFT_DOWN_MASK);
        } else {
            redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, modifier);
        }
        textArea.getInputMap().put(redoKeyStroke, "Redo");
        textArea.getActionMap().put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
    }

    /**
     * Adds undo/redo support to a text field with platform-specific shortcuts
     */
    private void addUndoRedoSupport(JTextField textField) {
        UndoManager undoManager = new UndoManager();
        textField.getDocument().addUndoableEditListener(undoManager);

        // Detect OS
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        int modifier = isMac ? KeyEvent.META_DOWN_MASK : KeyEvent.CTRL_DOWN_MASK;

        // Undo
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier);
        textField.getInputMap().put(undoKeyStroke, "Undo");
        textField.getActionMap().put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });

        // Redo
        KeyStroke redoKeyStroke;
        if (isMac) {
            redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier | KeyEvent.SHIFT_DOWN_MASK);
        } else {
            redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, modifier);
        }
        textField.getInputMap().put(redoKeyStroke, "Redo");
        textField.getActionMap().put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
    }

    /**
     * Prettifies JSON text
     */
    private String prettifyJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON: " + e.getMessage());
        }
    }

    /**
     * Updates the endpoints toggle button text with current count
     */
    private void updateEndpointsButton() {
        if (endpointsToggleButton != null) {
            int count = mockService.getMocksList().size();
            String currentText = endpointsToggleButton.getText();
            if (currentText.startsWith("▼")) {
                // Button is expanded, don't update to avoid closing it
                endpointsToggleButton.setText("▼ Collapse - Registered Endpoints (" + count + ")");
            } else {
                // Button is collapsed
                endpointsToggleButton.setText("▶ Expand - Registered Endpoints (" + count + ")");
            }
        }
    }

    /**
     * Updates the messages toggle button text with current count
     */
    private void updateMessagesButton() {
        if (messagesToggleButton != null) {
            int count = rabbitMQService.getSentMessages().size();
            String currentText = messagesToggleButton.getText();
            if (currentText.startsWith("▼")) {
                // Button is expanded, don't update to avoid closing it
                messagesToggleButton.setText("▼ Collapse - Sent Messages (" + count + ")");
            } else {
                // Button is collapsed
                messagesToggleButton.setText("▶ Expand - Sent Messages (" + count + ")");
            }
        }
    }

    /**
     * Sets the application icon for the window.
     * Order:
     * 1) Try multi-size icon set: icon-16.png, icon-32.png, icon-48.png, icon-64.png, icon-128.png
     * 2) Try single icon.png
     * 3) Fallback: programmatically generate emoji icon
     * Additionally updates OS taskbar/dock icon if supported.
     */
    private void setApplicationIcon() {
        try {
            java.util.List<Image> images = new java.util.ArrayList<>();
            int[] sizes = {16,32,48,64,128};
            for (int s : sizes) {
                String name = "icon-" + s + ".png";
                var url = getClass().getClassLoader().getResource(name);
                if (url != null) {
                    Image img = new ImageIcon(url).getImage();
                    images.add(img);
                    System.out.println("[ICON] Loaded size " + s + " from " + name);
                } else {
                    System.out.println("[ICON] Not found: " + name);
                }
            }
            if (images.isEmpty()) {
                // Try generic icon.png
                var single = getClass().getClassLoader().getResource("icon.png");
                if (single != null) {
                    Image img = new ImageIcon(single).getImage();
                    images.add(img);
                    System.out.println("[ICON] Loaded single icon.png");
                }
            }
            if (!images.isEmpty()) {
                // Apply all sizes (Swing will pick best for various DPI contexts)
                setIconImages(images);
                updateTaskbarIcon(images.get(images.size()-1)); // largest
                return;
            }
        } catch (Exception e) {
            System.out.println("[ICON] Failed loading from resources: " + e.getMessage());
        }
        // Fallback: programmatic emoji icon
        try {
            int size = 128; // create a reasonably large base image
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(new Color(63,81,181));
            g2d.fillOval(0,0,size,size);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font(UI_FONT, Font.BOLD, size/2));
            String emoji = "🚀";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (size - fm.stringWidth(emoji)) / 2;
            int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(emoji, x, y);
            g2d.dispose();
            setIconImage(image);
            updateTaskbarIcon(image);
            System.out.println("[ICON] Fallback emoji icon applied.");
        } catch (Exception ex) {
            System.out.println("[ICON] Fallback generation failed: " + ex.getMessage());
        }
    }

    /**
     * Updates taskbar/dock icon if platform supports it (Java 9+ Taskbar API).
     */
    private void updateTaskbarIcon(Image image) {
        try {
            // Java 9+ Taskbar API
            if (Taskbar.isTaskbarSupported()) {
                Taskbar tb = Taskbar.getTaskbar();
                if (tb.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    tb.setIconImage(image);
                    System.out.println("[ICON] Taskbar/Dock icon updated.");
                } else {
                    System.out.println("[ICON] Taskbar icon feature not supported.");
                }
            }
        } catch (Exception e) {
            System.out.println("[ICON] Taskbar update failed: " + e.getMessage());
        }
    }

    /**
     * Creates an expandable panel for listing registered endpoints
     */
    private JPanel createEndpointsListPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        // Toggle button
        endpointsToggleButton = new JButton("▶ Expand - Registered Endpoints (0)");
        endpointsToggleButton.setFont(new Font(UI_FONT, Font.PLAIN, 11));
        endpointsToggleButton.setBackground(new Color(245, 245, 245));
        endpointsToggleButton.setFocusPainted(false);
        endpointsToggleButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        endpointsToggleButton.setHorizontalAlignment(SwingConstants.LEFT);

        // List panel (initially hidden)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        scrollPane.setMinimumSize(new Dimension(200, 100));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setVisible(false);

        // Toggle action
        endpointsToggleButton.addActionListener(e -> {
            boolean isVisible = scrollPane.isVisible();
            scrollPane.setVisible(!isVisible);

            if (!isVisible) {
                // Expand - refresh list
                listPanel.removeAll();
                List<MockEndpoint> endpoints = mockService.getMocksList();
                System.out.println("🔍 DEBUG: Expanding endpoints list. Count: " + endpoints.size());
                for (MockEndpoint ep : endpoints) {
                    System.out.println("  - " + ep.getMethod() + " " + ep.getPath() + " (Status: " + ep.getStatusCode() + ")");
                }
                endpointsToggleButton.setText("▼ Collapse - Registered Endpoints (" + endpoints.size() + ")");

                if (endpoints.isEmpty()) {
                    JLabel emptyLabel = new JLabel("No endpoints registered yet");
                    emptyLabel.setFont(new Font(UI_FONT, Font.ITALIC, 11));
                    emptyLabel.setForeground(Color.GRAY);
                    emptyLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    listPanel.add(emptyLabel);
                } else {
                    for (MockEndpoint endpoint : endpoints) {
                        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
                        itemPanel.setBackground(Color.WHITE);
                        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                            new EmptyBorder(8, 10, 8, 10)
                        ));

                        // Info panel
                        JPanel infoPanel = new JPanel();
                        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                        infoPanel.setBackground(Color.WHITE);

                        JLabel methodPathLabel = new JLabel(endpoint.getMethod() + " " + endpoint.getPath());
                        methodPathLabel.setFont(new Font(MONO_FONT, Font.BOLD, 12));
                        methodPathLabel.setForeground(new Color(33, 150, 243));

                        JLabel statusLabel = new JLabel("Status: " + endpoint.getStatusCode());
                        statusLabel.setFont(new Font(UI_FONT, Font.PLAIN, 10));
                        statusLabel.setForeground(Color.GRAY);

                        infoPanel.add(methodPathLabel);
                        infoPanel.add(statusLabel);

                        // Delete button
                        JButton deleteBtn = new JButton("🗑️ Delete");
                        deleteBtn.setFont(new Font(UI_FONT, Font.PLAIN, 10));
                        deleteBtn.setBackground(new Color(244, 67, 54));
                        deleteBtn.setForeground(Color.WHITE);
                        deleteBtn.setFocusPainted(false);
                        deleteBtn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(211, 47, 47)),
                            new EmptyBorder(4, 12, 4, 12)
                        ));
                        deleteBtn.addActionListener(evt -> {
                            int confirm = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to delete this endpoint?\n\n" +
                                endpoint.getMethod() + " " + endpoint.getPath(),
                                "Delete Endpoint",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                            if (confirm == JOptionPane.YES_OPTION) {
                                boolean removed = mockService.removeMock(endpoint.getPath(), endpoint.getMethod());
                                if (removed) {
                                    logArea.append(String.format("🗑️ Endpoint deleted: %s %s\n",
                                        endpoint.getMethod(), endpoint.getPath()));
                                    logArea.setCaretPosition(logArea.getDocument().getLength());

                                    // Refresh the list
                                    endpointsToggleButton.doClick();
                                    endpointsToggleButton.doClick();
                                }
                            }
                        });

                        itemPanel.add(infoPanel, BorderLayout.CENTER);
                        itemPanel.add(deleteBtn, BorderLayout.EAST);
                        listPanel.add(itemPanel);
                    }
                }

                listPanel.revalidate();
                listPanel.repaint();
                scrollPane.revalidate();
                scrollPane.repaint();
                container.revalidate();
                container.repaint();
            } else {
                // Collapse
                endpointsToggleButton.setText("▶ Expand - Registered Endpoints (" + mockService.getMocksList().size() + ")");
            }
        });

        container.add(endpointsToggleButton, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    /**
     * Creates an expandable panel for listing sent messages
     */
    private JPanel createMessagesListPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        // Toggle button
        messagesToggleButton = new JButton("▶ Expand - Sent Messages (0)");
        messagesToggleButton.setFont(new Font(UI_FONT, Font.PLAIN, 11));
        messagesToggleButton.setBackground(new Color(245, 245, 245));
        messagesToggleButton.setFocusPainted(false);
        messagesToggleButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        messagesToggleButton.setHorizontalAlignment(SwingConstants.LEFT);

        // List panel (initially hidden)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        scrollPane.setMinimumSize(new Dimension(200, 100));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setVisible(false);

        // Toggle action
        messagesToggleButton.addActionListener(e -> {
            boolean isVisible = scrollPane.isVisible();
            scrollPane.setVisible(!isVisible);

            if (!isVisible) {
                // Expand - refresh list
                listPanel.removeAll();
                List<RabbitMQService.QueueMessage> messages = rabbitMQService.getSentMessages();
                messagesToggleButton.setText("▼ Collapse - Sent Messages (" + messages.size() + ")");

                if (messages.isEmpty()) {
                    JLabel emptyLabel = new JLabel("No messages sent yet");
                    emptyLabel.setFont(new Font(UI_FONT, Font.ITALIC, 11));
                    emptyLabel.setForeground(Color.GRAY);
                    emptyLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    listPanel.add(emptyLabel);
                } else {
                    for (RabbitMQService.QueueMessage message : messages) {
                        JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
                        itemPanel.setBackground(Color.WHITE);
                        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                            new EmptyBorder(8, 10, 8, 10)
                        ));

                        // Info panel
                        JPanel infoPanel = new JPanel();
                        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                        infoPanel.setBackground(Color.WHITE);

                        JLabel destLabel = new JLabel("📤 " + message.getDestination());
                        destLabel.setFont(new Font(MONO_FONT, Font.BOLD, 11));
                        destLabel.setForeground(new Color(255, 87, 34));

                        JLabel vhostLabel = new JLabel("VHost: " + (message.getVirtualHost() != null ? message.getVirtualHost() : "default"));
                        vhostLabel.setFont(new Font(UI_FONT, Font.PLAIN, 10));
                        vhostLabel.setForeground(Color.GRAY);

                        JLabel contentLabel = new JLabel("Content: " +
                            (message.getContent().length() > 50 ?
                                message.getContent().substring(0, 50) + "..." :
                                message.getContent()));
                        contentLabel.setFont(new Font(MONO_FONT, Font.PLAIN, 10));
                        contentLabel.setForeground(new Color(100, 100, 100));

                        infoPanel.add(destLabel);
                        infoPanel.add(vhostLabel);
                        infoPanel.add(contentLabel);

                        itemPanel.add(infoPanel, BorderLayout.CENTER);
                        listPanel.add(itemPanel);
                    }
                }

                listPanel.revalidate();
                listPanel.repaint();
                scrollPane.revalidate();
                scrollPane.repaint();
                container.revalidate();
                container.repaint();
            } else {
                // Collapse
                messagesToggleButton.setText("▶ Expand - Sent Messages (" + rabbitMQService.getSentMessages().size() + ")");
            }
        });

        container.add(messagesToggleButton, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }
}
