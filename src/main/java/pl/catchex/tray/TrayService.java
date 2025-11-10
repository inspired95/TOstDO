package pl.catchex.tray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class TrayService {

    private static final Logger logger = LoggerFactory.getLogger(TrayService.class);
    private TrayIcon trayIcon;

    public TrayService() {
        EventQueue.invokeLater(this::initializeTray);
    }

    private void initializeTray() {
        if (!SystemTray.isSupported()) {
            logger.warn("SystemTray is not supported on this platform.");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();

        // Icon file required. Add it to resources.
        // In this example we try to load "icon.png" from the classpath.
        Image image = TrayImageProvider.createImage(getClass().getClassLoader(), "icon.png");

        // Context menu
        PopupMenu trayMenu = new PopupMenu();

        MenuItem exitItem = new MenuItem("Exit TOstDO");
        exitItem.addActionListener(e -> {
            tray.remove(trayIcon); // Remove icon
            System.exit(0); // Exit application (can be replaced with assembler.stop())
        });
        trayMenu.add(exitItem);

        // Create tray icon
        this.trayIcon = new TrayIcon(image, "TOstDO", trayMenu);
        this.trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
            logger.info("SystemTray icon added.");

            // Show startup notification
            showNotification("TOstDO", "Application started and is monitoring the file.", TrayIcon.MessageType.INFO);

        } catch (AWTException e) {
            logger.error("Failed to add icon to SystemTray", e);
        }
    }

    /**
     * Displays a system notification.
     *
     * @param title   Notification title
     * @param message Notification message
     * @param type    Type (INFO, WARNING, ERROR)
     */
    public void showNotification(String title, String message, TrayIcon.MessageType type) {
        if (trayIcon == null) {
            logger.warn("Attempted to send notification but TrayIcon is not initialized.");
            return;
        }

        // Use AWT event queue to display the notification
        EventQueue.invokeLater(() -> {
            trayIcon.displayMessage(title, message, type);
            logger.info("Sent notification: title='{}'", title);
        });
    }

    /**
     * Remove icon from the system tray and clean up resources (non-blocking).
     * Can be called during application shutdown.
     */
    public void stop() {
        // Remove icon on the AWT thread
        try {
            EventQueue.invokeLater(this::removeTrayIconSafely);
        } catch (Exception e) {
            logger.debug("Unable to initialize EventQueue in TrayService.stop(): {}", e.getMessage());
        }
    }

    // Extracted from stop() to satisfy static analysis and simplify the lambda
    private void removeTrayIconSafely() {
        try {
            if (trayIcon != null && SystemTray.isSupported()) {
                doRemoveTrayIcon();
            }
        } catch (Exception e) {
            logger.debug("Exception during TrayService.stop(): {}", e.getMessage());
        }
    }

    // Separated out to avoid nested try/catch inside removeTrayIconSafely
    private void doRemoveTrayIcon() {
        try {
            SystemTray.getSystemTray().remove(trayIcon);
        } catch (Exception ex) {
            logger.debug("Error while removing TrayIcon: {}", ex.getMessage());
        }
        trayIcon = null;
        logger.info("TrayService: icon removed from SystemTray.");
    }
}
