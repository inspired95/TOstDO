package pl.catchex.tray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
        Image image = createImage("icon.png");
        if (image == null) {
            logger.error("Cannot load icon image for SystemTray.");
            return;
        }

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
            EventQueue.invokeLater(() -> {
                try {
                    if (trayIcon != null && SystemTray.isSupported()) {
                        try {
                            SystemTray.getSystemTray().remove(trayIcon);
                        } catch (Exception ex) {
                            logger.debug("Error while removing TrayIcon: {}", ex.getMessage());
                        }
                        trayIcon = null;
                        logger.info("TrayService: icon removed from SystemTray.");
                    }
                } catch (Throwable t) {
                    logger.debug("Exception during TrayService.stop(): {}", t.getMessage());
                }
            });
        } catch (Throwable t) {
            logger.debug("Unable to initialize EventQueue in TrayService.stop(): {}", t.getMessage());
        }
    }

    // Helper to safely load an image from resources (ImageIO.read + fallback)
    private Image createImage(String resourcePath) {
        // First try to open the resource as a stream using try-with-resources
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.warn("Icon resource not found: {}. Using generated fallback.", resourcePath);
                return createFallbackImage();
            }

            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                // If ImageIO cannot read the format
                logger.warn("ImageIO.read returned null for resource: {}. Using fallback.", resourcePath);
                return createFallbackImage();
            }
            return img;
        } catch (IOException e) {
            logger.warn("Error loading image resource: {}. Using fallback. Error: {}", resourcePath, e.getMessage());
            return createFallbackImage();
        }
    }

    // Create a simple generated fallback image (e.g., 16x16 with fill)
    private Image createFallbackImage() {
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            // Transparent background
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, size, size);
            g.setComposite(AlphaComposite.SrcOver);

            // Rounded rectangle and a simple glyph (e.g., letter T)
            g.setColor(new Color(0x2E86AB)); // blue
            g.fillRoundRect(0, 0, size, size, 4, 4);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Dialog", Font.BOLD, 10));
            g.drawString("T", 4, 12);
        } finally {
            g.dispose();
        }
        return img;
    }
}
