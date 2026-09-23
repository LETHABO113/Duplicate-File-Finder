package com.dupfinder.ui.gui;

import com.dupfinder.core.AppController;

import javax.swing.*;

/**
 * Owner: Member 6 (Kungawo) - GUI Developer
 *
 * Swing GUI:
 *   - "Choose folder" button
 *   - Progress bar while scanning (run the scan on a SwingWorker /
 *     background thread so the UI does not freeze)
 *   - Table of duplicate groups, sorted by wasted space
 *   - Checkboxes to select which files to act on
 *   - Quarantine, Restore, Export buttons
 *
 * TODO (Member 6):
 *   - Build the layout (JFrame, JTable with checkbox column, JProgressBar,
 *     JButton toolbar, JFileChooser for "Choose folder").
 *   - Use SwingWorker to call AppController.runScan() off the Event Dispatch
 *     Thread, and publish progress back to the JProgressBar.
 *   - Wire Quarantine / Restore / Export buttons to AppController.
 */
public class MainWindow extends JFrame {

    private final AppController controller;

    public MainWindow(AppController controller) {
        this.controller = controller;
        setTitle("Duplicate File Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JLabel placeholder = new JLabel("TODO: build GUI layout here (Member 6)", SwingConstants.CENTER);
        add(placeholder);
    }

    public void display() {
        setVisible(true);
    }
}
