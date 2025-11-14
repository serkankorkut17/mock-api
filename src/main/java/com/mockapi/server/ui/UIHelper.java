package com.mockapi.server.ui;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.event.KeyEvent;

public class UIHelper {

    /**
     * Get platform-specific UI font
     */
    public static String getSystemUIFont() {
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
    public static String getSystemMonoFont() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return "SF Mono"; // macOS monospace font
        } else if (os.contains("win")) {
            return "Consolas"; // Windows monospace font
        } else {
            return "Monospaced"; // Linux fallback
        }
    }

    /**
     * Adds undo/redo support to a text area with platform-specific shortcuts
     * Windows/Linux: Ctrl+Z (undo), Ctrl+Y (redo)
     * macOS: Cmd+Z (undo), Cmd+Shift+Z (redo)
     */
    public static void addUndoRedoSupport(JTextArea textArea) {
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
    public static void addUndoRedoSupport(JTextField textField) {
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
}

