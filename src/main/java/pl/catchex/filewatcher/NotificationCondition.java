package pl.catchex.filewatcher;

/**
 * A strategy interface that defines whether a notification
 * should be processed or suppressed.
 */
public interface NotificationCondition {

    /**
     * Checks if a notification should be allowed to proceed.
     *
     * @return true if the notification should fire,
     * false if it should be suppressed.
     */
    boolean shouldNotify();
}
