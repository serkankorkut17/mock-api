package com.mockapi.server.ui;

import com.mockapi.server.service.MockService;
import com.mockapi.server.service.RabbitMQService;
import com.mockapi.server.service.TemplateService;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

@Component
@Profile("ui")
public class MockApiFrame extends JFrame {

    private static final String UI_FONT = UIHelper.getSystemUIFont();

    private final MockService mockService;
    private final RabbitMQService rabbitMQService;
    private final TemplateService templateService;

    private LogPanel logPanel;
    private RestApiPanel restApiPanel;
    private RabbitMQPanel rabbitMQPanel;

    public MockApiFrame(MockService mockService, RabbitMQService rabbitMQService, TemplateService templateService) {
        this.mockService = mockService;
        this.rabbitMQService = rabbitMQService;
        this.templateService = templateService;

        // Window configuration
        setTitle("🚀 Mock API Manager");
        setSize(1100, 750);
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

        // Create log panel first (it will be used by other panels)
        logPanel = new LogPanel();

        // Form panel (Mock API) - using RestApiPanel component
        restApiPanel = new RestApiPanel(mockService, templateService, logPanel::appendLog);

        // RabbitMQ panel - using RabbitMQPanel component
        rabbitMQPanel = new RabbitMQPanel(rabbitMQService, logPanel::appendLog);

        contentPanel.add(restApiPanel);
        contentPanel.add(rabbitMQPanel);

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
}

