package pl.catchex.tray;

import java.awt.TrayIcon;

@FunctionalInterface
public interface NotificationSender {
    void send(String title, String message, TrayIcon.MessageType type);
}

