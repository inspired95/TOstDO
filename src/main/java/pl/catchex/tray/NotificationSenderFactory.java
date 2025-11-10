package pl.catchex.tray;

/**
 * Factory that provides a NotificationSender implementation and (optionally)
 * the created TrayService instance if the factory created one.
 * Use NotificationSenderFactory.createDefault() to attempt to create
 * a real tray (if SystemTray is available). On failure the returned Provider
 * has sender==null which indicates GUI notifications are not available.
 */
public final class NotificationSenderFactory {

    private NotificationSenderFactory() {
        // util
    }

    public record Provider(NotificationSender notificationSender, TrayService trayService) {
        /**
         * Returns the NotificationSender (may be null if GUI creation failed).
         */
        public NotificationSender getNotificationSender() {
            return notificationSender;
        }

        /**
         * Returns the created TrayService instance if the factory created one (otherwise null).
         */
        public TrayService getTrayService() {
            return trayService;
        }
    }

    /**
     * Attempts to create a TrayService and return a method reference as NotificationSender.
     * In case of error (e.g. SystemTray not supported) the returned Provider has sender==null.
     */
    public static Provider createDefaultTrayService() {
        try {
            TrayService trayService = new TrayService();
            return new Provider(trayService::showNotification, trayService);
        } catch (Exception e) {
            // Don't propagate the exception: if GUI creation failed, return no sender
            return new Provider(null, null);
        }
    }

    /**
     * Returns a Provider with a noop sender (useful for tests).
     */
    public static Provider createNoop() {
        return new Provider((title, message, type) -> {
            // noop
        }, null);
    }
}
