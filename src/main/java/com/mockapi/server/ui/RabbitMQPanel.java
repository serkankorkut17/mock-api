package com.mockapi.server.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mockapi.server.service.RabbitMQService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class RabbitMQPanel extends JPanel {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        com.fasterxml.jackson.core.util.DefaultPrettyPrinter printer = new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();
        printer.indentArraysWith(com.fasterxml.jackson.core.util.DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        objectMapper.setDefaultPrettyPrinter(printer);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private static final String UI_FONT = UIHelper.getSystemUIFont();
    private static final String MONO_FONT = UIHelper.getSystemMonoFont();

    private final RabbitMQService rabbitMQService;
    private final Consumer<String> logConsumer;

    private JTextField queueField;
    private JTextField vhostField;
    private JTextField exchangeField;
    private JTextField routingField;
    private JComboBox<String> contentTypeBox;
    private JTextArea rabbitMessageArea;
    private JButton messagesToggleButton;
    private JPanel messagesListPanel;

    public RabbitMQPanel(RabbitMQService rabbitMQService, Consumer<String> logConsumer) {
        this.rabbitMQService = rabbitMQService;
        this.logConsumer = logConsumer;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "RabbitMQ Messaging", TitledBorder.LEFT, TitledBorder.TOP,
                new Font(UI_FONT, Font.BOLD, 14), new Color(255, 87, 34)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        initComponents();
    }

    private void initComponents() {
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
        UIHelper.addUndoRedoSupport(queueField);
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
        UIHelper.addUndoRedoSupport(vhostField);
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
        UIHelper.addUndoRedoSupport(exchangeField);
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
        UIHelper.addUndoRedoSupport(routingField);
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
        UIHelper.addUndoRedoSupport(rabbitMessageArea);
        JScrollPane messageScroll = new JScrollPane(rabbitMessageArea);

        // Small prettify button overlay in bottom-right corner
        JButton rabbitPrettifyButton = new JButton("✨");
        rabbitPrettifyButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        rabbitPrettifyButton.setOpaque(true);
        rabbitPrettifyButton.setBackground(new Color(103, 58, 183));
        rabbitPrettifyButton.setForeground(Color.WHITE);
        rabbitPrettifyButton.setFocusPainted(false);
        rabbitPrettifyButton.setBorderPainted(false);
        rabbitPrettifyButton.setContentAreaFilled(true);
        rabbitPrettifyButton.setToolTipText("Format JSON");
        rabbitPrettifyButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(81, 45, 168)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        rabbitPrettifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rabbitPrettifyButton.addActionListener(e -> prettifyJsonArea(rabbitMessageArea));

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
        clearButton.setOpaque(true);
        clearButton.setBackground(new Color(240, 240, 240));
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(true);
        clearButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 20, 8, 20)
        ));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> clearFields());

        JButton sendButton = new JButton("→ Send Message");
        sendButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 12));
        sendButton.setOpaque(true);
        sendButton.setBackground(new Color(255, 87, 34));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(true);
        sendButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 74, 25)),
            new EmptyBorder(8, 25, 8, 25)
        ));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());

        buttonPanel.add(clearButton);
        buttonPanel.add(sendButton);

        add(fieldsPanel, BorderLayout.CENTER);

        // Bottom section with both buttons and messages list
        JPanel bottomSection = new JPanel(new BorderLayout(5, 5));
        bottomSection.setBackground(Color.WHITE);
        bottomSection.add(buttonPanel, BorderLayout.NORTH);
        bottomSection.add(createMessagesListPanel(), BorderLayout.CENTER);

        add(bottomSection, BorderLayout.SOUTH);
    }

    private void clearFields() {
        queueField.setText("test-queue");
        vhostField.setText("cert");
        exchangeField.setText("");
        routingField.setText("");
        rabbitMessageArea.setText("{\n  \"message\": \"Hello\"\n}");
    }

    private void sendMessage() {
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
                logConsumer.accept(logMessage);
            } else {
                rabbitMQService.sendMessageToQueue(queue, message, vhost.isEmpty() ? null : vhost, asJson);
                String logMessage = String.format("📤 Message sent to Queue: %s (VHost: %s, Type: %s)\n", queue, vhost.isEmpty() ? "default" : vhost, contentType);
                logConsumer.accept(logMessage);
            }

            // Update the messages toggle button count
            updateMessagesButton();

            JOptionPane.showMessageDialog(this,
                "Message sent successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            String errorLog = String.format("❌ Error: %s\n", ex.getMessage());
            logConsumer.accept(errorLog);

            JOptionPane.showMessageDialog(this,
                "Failed to send message:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createMessagesListPanel() {
        messagesListPanel = new JPanel(new BorderLayout());
        messagesListPanel.setBackground(Color.WHITE);

        // Toggle button
        messagesToggleButton = new JButton("▶ Expand - Sent Messages (0)");
        messagesToggleButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
        messagesToggleButton.setOpaque(true);
        messagesToggleButton.setBackground(new Color(245, 245, 245));
        messagesToggleButton.setFocusPainted(false);
        messagesToggleButton.setBorderPainted(false);
        messagesToggleButton.setContentAreaFilled(true);
        messagesToggleButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        messagesToggleButton.setHorizontalAlignment(SwingConstants.LEFT);
        messagesToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

                        JLabel destLabel = new JLabel(message.getDestination());
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
                messagesListPanel.revalidate();
                messagesListPanel.repaint();
            } else {
                // Collapse
                messagesToggleButton.setText("▶ Expand - Sent Messages (" + rabbitMQService.getSentMessages().size() + ")");
            }
        });

        messagesListPanel.add(messagesToggleButton, BorderLayout.NORTH);
        messagesListPanel.add(scrollPane, BorderLayout.CENTER);

        return messagesListPanel;
    }

    private void updateMessagesList(JPanel listContent) {
        listContent.removeAll();
        List<RabbitMQService.QueueMessage> messages = rabbitMQService.getSentMessages();

        if (messages.isEmpty()) {
            JLabel emptyLabel = new JLabel("No messages sent yet");
            emptyLabel.setFont(new Font(UI_FONT, Font.ITALIC, 11));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
            listContent.add(emptyLabel);
        } else {
            for (RabbitMQService.QueueMessage msg : messages) {
                JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
                messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                messagePanel.setBackground(Color.WHITE);
                messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    new EmptyBorder(5, 10, 5, 10)
                ));

                String destination = msg.getDestination();

                JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
                infoPanel.setOpaque(false);

                JLabel destLabel = new JLabel(destination);
                destLabel.setFont(new Font(MONO_FONT, Font.BOLD, 11));

                String messagePreview = msg.getContent();
                if (messagePreview.length() > 50) {
                    messagePreview = messagePreview.substring(0, 50) + "...";
                }
                JLabel msgLabel = new JLabel(messagePreview);
                msgLabel.setFont(new Font(MONO_FONT, Font.PLAIN, 10));
                msgLabel.setForeground(Color.GRAY);

                infoPanel.add(destLabel);
                infoPanel.add(msgLabel);

                messagePanel.add(infoPanel, BorderLayout.CENTER);

                listContent.add(messagePanel);
            }
        }

        listContent.revalidate();
        listContent.repaint();
    }

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

    private void prettifyJsonArea(JTextArea area) {
        try {
            String formatted = prettifyJson(area.getText());
            area.setText(formatted);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "JSON Format Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String prettifyJson(String json) throws Exception {
        Object obj = objectMapper.readValue(json, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}

