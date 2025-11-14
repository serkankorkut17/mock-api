package com.mockapi.server.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mockapi.server.service.EndpointTemplate;
import com.mockapi.server.service.MockEndpoint;
import com.mockapi.server.service.MockService;
import com.mockapi.server.service.TemplateCategory;
import com.mockapi.server.service.TemplateService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class RestApiPanel extends JPanel {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        com.fasterxml.jackson.core.util.DefaultPrettyPrinter printer = new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();
        printer.indentArraysWith(com.fasterxml.jackson.core.util.DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        objectMapper.setDefaultPrettyPrinter(printer);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private static final String UI_FONT = UIHelper.getSystemUIFont();
    private static final String MONO_FONT = UIHelper.getSystemMonoFont();

    private final MockService mockService;
    private final TemplateService templateService;
    private final Consumer<String> logConsumer;

    private JTextField pathField;
    private JComboBox<String> methodBox;
    private JComboBox<String> statusBox;
    private JComboBox<String> contentTypeBox;
    private JTextArea jsonArea;
    private JButton endpointsToggleButton;
    private JPanel endpointsListPanel;

    public RestApiPanel(MockService mockService, TemplateService templateService, Consumer<String> logConsumer) {
        this.mockService = mockService;
        this.templateService = templateService;
        this.logConsumer = logConsumer;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Mock API Configuration", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font(UI_FONT, Font.BOLD, 14), new Color(63, 81, 181)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        initComponents();
    }

    private void initComponents() {
        // Create template menu once for all fields
        JPopupMenu templateMenu = createTemplateMenu();

        // Fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setComponentPopupMenu(templateMenu);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Path field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel pathLabel = new JLabel("Endpoint Path:");
        pathLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(pathLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        pathField = new JTextField("/api/test");
        pathField.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        pathField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 8, 5, 8)
        ));
        UIHelper.addUndoRedoSupport(pathField);
        pathField.setComponentPopupMenu(templateMenu);
        fieldsPanel.add(pathField, gbc);

        // Method field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel methodLabel = new JLabel("HTTP Method:");
        methodLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(methodLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        methodBox = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE", "PATCH"});
        methodBox.setFont(new Font(UI_FONT, Font.PLAIN, 12));
        methodBox.setComponentPopupMenu(templateMenu);
        fieldsPanel.add(methodBox, gbc);

        // Status Code field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel statusLabel = new JLabel("Status Code:");
        statusLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        statusBox = new JComboBox<>(new String[]{
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
        statusBox.setComponentPopupMenu(templateMenu);
        fieldsPanel.add(statusBox, gbc);

        // Content-Type field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel contentTypeLabel = new JLabel("Content-Type:");
        contentTypeLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(contentTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        contentTypeBox = new JComboBox<>(new String[]{
                "application/json",
                "application/x-www-form-urlencoded",
                "application/xml",
                "text/plain",
                "text/html",
                "multipart/form-data"
        });
        contentTypeBox.setFont(new Font(UI_FONT, Font.PLAIN, 12));
        contentTypeBox.setComponentPopupMenu(templateMenu);
        fieldsPanel.add(contentTypeBox, gbc);

        // Response JSON field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel jsonLabel = new JLabel("Response JSON:");
        jsonLabel.setFont(new Font(UI_FONT, Font.BOLD, 12));
        fieldsPanel.add(jsonLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        // Container with relative positioning for overlay button
        JPanel jsonContainer = new JPanel(new BorderLayout());
        jsonContainer.setBackground(Color.WHITE);
        jsonContainer.setComponentPopupMenu(templateMenu);

        jsonArea = new JTextArea(8, 30);
        jsonArea.setText("{\n  \"status\": \"success\",\n  \"message\": \"Hello from Mock API\",\n  \"timestamp\": \"2025-11-13T10:00:00Z\"\n}");
        jsonArea.setFont(new Font(MONO_FONT, Font.PLAIN, 12));
        jsonArea.setLineWrap(true);
        jsonArea.setWrapStyleWord(true);
        jsonArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 8, 5, 8)
        ));
        UIHelper.addUndoRedoSupport(jsonArea);
        JScrollPane jsonScroll = new JScrollPane(jsonArea);

        // Small prettify button overlay in bottom-right corner
        JButton prettifyButton = new JButton("✨");
        prettifyButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        prettifyButton.setBackground(new Color(103, 58, 183));
        prettifyButton.setForeground(Color.WHITE);
        prettifyButton.setFocusPainted(false);
        prettifyButton.setToolTipText("Format JSON");
        prettifyButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(81, 45, 168)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        prettifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prettifyButton.addActionListener(e -> prettifyJsonArea(jsonArea));

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
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> clearFields());

        JButton addButton = new JButton("✓ Add Mock");
        addButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 12));
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 142, 60)),
                new EmptyBorder(8, 25, 8, 25)
        ));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addMockEndpoint());

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);

        add(fieldsPanel, BorderLayout.CENTER);

        // Bottom section with both buttons and endpoints list
        JPanel bottomSection = new JPanel(new BorderLayout(5, 5));
        bottomSection.setBackground(Color.WHITE);
        bottomSection.add(buttonPanel, BorderLayout.NORTH);
        bottomSection.add(createEndpointsListPanel(), BorderLayout.CENTER);

        add(bottomSection, BorderLayout.SOUTH);
    }

    private void clearFields() {
        pathField.setText("/api/");
        methodBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        jsonArea.setText("{\n  \"message\": \"Response\"\n}");
    }

    private void addMockEndpoint() {
        String path = pathField.getText().trim();
        String method = (String) methodBox.getSelectedItem();
        String statusText = (String) statusBox.getSelectedItem();
        int statusCode = Integer.parseInt(statusText.split(" ")[0]);
        String json = jsonArea.getText().trim();
        String contentType = (String) contentTypeBox.getSelectedItem();

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Path cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mockService.addMock(path, method, statusCode, json, contentType);

        System.out.println("DEBUG: Mock added - " + method + " " + path + " (Status: " + statusCode + ")");
        System.out.println("DEBUG: Total endpoints now: " + mockService.getMocksList().size());

        String logMessage = String.format("✅ Mock added: %s %s → %d\n", method, path, statusCode);
        logConsumer.accept(logMessage);

        // Update the endpoints toggle button count
        updateEndpointsButton();

        JOptionPane.showMessageDialog(this,
                String.format("Mock endpoint created!\n\nEndpoint: %s\nMethod: %s\nStatus: %d", path, method, statusCode),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createEndpointsListPanel() {
        endpointsListPanel = new JPanel(new BorderLayout());
        endpointsListPanel.setBackground(Color.WHITE);

        // Toggle button
        endpointsToggleButton = new JButton("▶ Expand - Registered Endpoints (0)");
        endpointsToggleButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
        endpointsToggleButton.setBackground(new Color(245, 245, 245));
        endpointsToggleButton.setFocusPainted(false);
        endpointsToggleButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        endpointsToggleButton.setHorizontalAlignment(SwingConstants.LEFT);
        endpointsToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // List panel (initially hidden)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(248, 249, 250));
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setMinimumSize(new Dimension(400, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225)));
        scrollPane.setVisible(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);


        // Toggle action
        endpointsToggleButton.addActionListener(e -> {
            boolean isVisible = scrollPane.isVisible();
            scrollPane.setVisible(!isVisible);

            if (!isVisible) {
                // Expand - refresh list
                listPanel.removeAll();
                List<MockEndpoint> endpoints = mockService.getMocksList();
                System.out.println("DEBUG: Expanding endpoints list. Count: " + endpoints.size());
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
                        JPanel itemPanel = new JPanel(new BorderLayout(10, 5));
                        itemPanel.setBackground(new Color(250, 250, 252));
                        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(220, 220, 225), 1),
                                new EmptyBorder(12, 15, 12, 15)
                        ));
                        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                        // Hover effect
                        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseEntered(java.awt.event.MouseEvent evt) {
                                itemPanel.setBackground(new Color(245, 247, 250));
                                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(new Color(63, 81, 181), 1),
                                        new EmptyBorder(12, 15, 12, 15)
                                ));
                            }

                            public void mouseExited(java.awt.event.MouseEvent evt) {
                                itemPanel.setBackground(new Color(250, 250, 252));
                                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(new Color(220, 220, 225), 1),
                                        new EmptyBorder(12, 15, 12, 15)
                                ));
                            }
                        });

                        // Left panel with method badge
                        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
                        leftPanel.setOpaque(false);

                        // Method badge
                        JLabel methodBadge = new JLabel(endpoint.getMethod());
                        methodBadge.setFont(new Font(UI_FONT, Font.BOLD, 10));
                        methodBadge.setHorizontalAlignment(SwingConstants.CENTER);
                        methodBadge.setOpaque(true);
                        methodBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

                        // Color based on method
                        switch (endpoint.getMethod()) {
                            case "GET":
                                methodBadge.setBackground(new Color(76, 175, 80));
                                break;
                            case "POST":
                                methodBadge.setBackground(new Color(33, 150, 243));
                                break;
                            case "PUT":
                                methodBadge.setBackground(new Color(255, 152, 0));
                                break;
                            case "DELETE":
                                methodBadge.setBackground(new Color(244, 67, 54));
                                break;
                            case "PATCH":
                                methodBadge.setBackground(new Color(156, 39, 176));
                                break;
                            default:
                                methodBadge.setBackground(new Color(96, 125, 139));
                        }
                        methodBadge.setForeground(Color.WHITE);

                        leftPanel.add(methodBadge, BorderLayout.WEST);

                        // Info panel
                        JPanel infoPanel = new JPanel();
                        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                        infoPanel.setOpaque(false);

                        JLabel pathLabel = new JLabel(endpoint.getPath());
                        pathLabel.setFont(new Font(MONO_FONT, Font.BOLD, 13));
                        pathLabel.setForeground(new Color(33, 33, 33));

                        JLabel detailsLabel = new JLabel(String.format("Status: %d  •  Content-Type: %s",
                                endpoint.getStatusCode(),
                                endpoint.getContentType() != null ? endpoint.getContentType() : "application/json"));
                        detailsLabel.setFont(new Font(UI_FONT, Font.PLAIN, 10));
                        detailsLabel.setForeground(new Color(117, 117, 117));

                        infoPanel.add(pathLabel);
                        infoPanel.add(Box.createVerticalStrut(3));
                        infoPanel.add(detailsLabel);

                        leftPanel.add(infoPanel, BorderLayout.CENTER);

                        // Delete button with modern design
                        JButton deleteBtn = new JButton("✕");
                        deleteBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
                        deleteBtn.setPreferredSize(new Dimension(36, 36));
                        deleteBtn.setBackground(new Color(244, 67, 54));
                        deleteBtn.setForeground(Color.WHITE);
                        deleteBtn.setFocusPainted(false);
                        deleteBtn.setBorder(BorderFactory.createEmptyBorder());
                        deleteBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                        deleteBtn.setToolTipText("Delete this endpoint");

                        // Hover effect for delete button
                        deleteBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseEntered(java.awt.event.MouseEvent evt) {
                                deleteBtn.setBackground(new Color(211, 47, 47));
                            }

                            public void mouseExited(java.awt.event.MouseEvent evt) {
                                deleteBtn.setBackground(new Color(244, 67, 54));
                            }
                        });

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
                                    logConsumer.accept(String.format("🗑️ Endpoint deleted: %s %s\n",
                                            endpoint.getMethod(), endpoint.getPath()));

                                    // Refresh the list
                                    endpointsToggleButton.doClick();
                                    endpointsToggleButton.doClick();
                                }
                            }
                        });

                        itemPanel.add(leftPanel, BorderLayout.CENTER);
                        itemPanel.add(deleteBtn, BorderLayout.EAST);
                        listPanel.add(itemPanel);
                        listPanel.add(Box.createVerticalStrut(8)); // Space between items
                    }
                }

                listPanel.revalidate();
                listPanel.repaint();
                scrollPane.revalidate();
                scrollPane.repaint();
                endpointsListPanel.revalidate();
                endpointsListPanel.repaint();
            } else {
                // Collapse
                endpointsToggleButton.setText("▶ Expand - Registered Endpoints (" + mockService.getMocksList().size() + ")");
            }
        });

        endpointsListPanel.add(endpointsToggleButton, BorderLayout.NORTH);
        endpointsListPanel.add(scrollPane, BorderLayout.CENTER);

        return endpointsListPanel;
    }

    private void updateEndpointsList(JPanel listContent) {
        listContent.removeAll();
        List<MockEndpoint> endpoints = mockService.getMocksList();

        if (endpoints.isEmpty()) {
            JLabel emptyLabel = new JLabel("No endpoints configured yet");
            emptyLabel.setFont(new Font(UI_FONT, Font.ITALIC, 11));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
            listContent.add(emptyLabel);
        } else {
            for (MockEndpoint endpoint : endpoints) {
                JPanel endpointPanel = new JPanel(new BorderLayout(5, 5));
                endpointPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                endpointPanel.setBackground(Color.WHITE);
                endpointPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                        new EmptyBorder(5, 10, 5, 10)
                ));

                String methodColor = getMethodColor(endpoint.getMethod());
                JLabel methodLabel = new JLabel(endpoint.getMethod());
                methodLabel.setFont(new Font(MONO_FONT, Font.BOLD, 10));
                methodLabel.setForeground(Color.decode(methodColor));
                methodLabel.setPreferredSize(new Dimension(60, 20));

                JLabel pathLabel = new JLabel(endpoint.getPath());
                pathLabel.setFont(new Font(MONO_FONT, Font.PLAIN, 11));

                JLabel statusLabel = new JLabel(String.valueOf(endpoint.getStatusCode()));
                statusLabel.setFont(new Font(MONO_FONT, Font.PLAIN, 10));
                statusLabel.setForeground(endpoint.getStatusCode() >= 400 ? Color.RED : new Color(76, 175, 80));

                JButton deleteButton = new JButton("✕");
                deleteButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 10));
                deleteButton.setBackground(new Color(244, 67, 54));
                deleteButton.setForeground(Color.WHITE);
                deleteButton.setFocusPainted(false);
                deleteButton.setBorder(new EmptyBorder(2, 8, 2, 8));
                deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                deleteButton.addActionListener(e -> {
                    mockService.removeMock(endpoint.getPath(), endpoint.getMethod());
                    updateEndpointsList(listContent);
                    updateEndpointsButton();
                    listContent.revalidate();
                    listContent.repaint();
                });

                JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                leftPanel.setOpaque(false);
                leftPanel.add(methodLabel);
                leftPanel.add(pathLabel);

                JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                rightPanel.setOpaque(false);
                rightPanel.add(statusLabel);
                rightPanel.add(deleteButton);

                endpointPanel.add(leftPanel, BorderLayout.WEST);
                endpointPanel.add(rightPanel, BorderLayout.EAST);

                listContent.add(endpointPanel);
            }
        }

        listContent.revalidate();
        listContent.repaint();
    }

    private String getMethodColor(String method) {
        return switch (method) {
            case "GET" -> "#2196F3";
            case "POST" -> "#4CAF50";
            case "PUT" -> "#FF9800";
            case "DELETE" -> "#F44336";
            case "PATCH" -> "#9C27B0";
            default -> "#666666";
        };
    }

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

    private JPopupMenu createTemplateMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(Color.WHITE);
        menu.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel menuTitle = new JLabel(" 📋 API Templates ");
        menuTitle.setFont(new Font(UI_FONT, Font.BOLD, 12));
        menuTitle.setForeground(new Color(63, 81, 181));
        menuTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
        menu.add(menuTitle);
        menu.addSeparator();

        List<TemplateCategory> categories = templateService.getCategories();

        for (TemplateCategory category : categories) {
            JMenu categoryMenu = new JMenu(category.getName());
            categoryMenu.setFont(new Font(UI_FONT, Font.PLAIN, 11));

            for (EndpointTemplate template : category.getEndpoints()) {
                JMenuItem item = new JMenuItem(
                        template.getName() != null && !template.getName().isEmpty()
                                ? template.getName()
                                : template.getMethod() + " " + template.getPath() + " (" + template.getStatusCode() + ")"
                );

                item.setFont(new Font(UI_FONT, Font.PLAIN, 11));

                // Set tooltip with full details including method and status
                String method = template.getMethod();
                item.setToolTipText(String.format(
                        "<html><b>%s</b> %s<br>Status: %d<br>Click to load</html>",
                        method, template.getPath(), template.getStatusCode()
                ));

                item.addActionListener(e -> applyTemplate(template));
                categoryMenu.add(item);
            }

            menu.add(categoryMenu);
        }

        return menu;
    }

    private void applyTemplate(EndpointTemplate template) {
        pathField.setText(template.getPath());
        methodBox.setSelectedItem(template.getMethod());

        for (int i = 0; i < statusBox.getItemCount(); i++) {
            String item = statusBox.getItemAt(i);
            if (item.startsWith(String.valueOf(template.getStatusCode()))) {
                statusBox.setSelectedIndex(i);
                break;
            }
        }

        contentTypeBox.setSelectedItem(template.getContentType());
        jsonArea.setText(template.getResponse());

        logConsumer.accept(String.format("📋 Template applied: %s %s\n", template.getMethod(), template.getPath()));
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

