package pl.catchex.filewatcher;

import java.util.concurrent.atomic.AtomicLong;
import java.time.Clock;

/**
 * An implementation of NotificationCondition that enforces a "debounce"
 * or "quiet period." It only returns true if a certain amount of time
 * has passed since the last successful notification.
 * This class is thread-safe.
 */
public class DebounceCondition implements NotificationCondition {

    private final long debouncePeriodMs;
    private final AtomicLong lastNotificationTime = new AtomicLong(0);
    private final Clock clock;

    /**
     * Creates a new debouncing condition with a custom clock (useful for tests).
     *
     * @param debouncePeriodMs The minimum time (in milliseconds) that must
     *                         pass between successful notifications.
     * @param clock            Clock used to obtain current time in milliseconds.
     */
    public DebounceCondition(long debouncePeriodMs, Clock clock) {
        this.debouncePeriodMs = debouncePeriodMs;
        this.clock = clock;
        // Initialize lastNotificationTime so that the first call to shouldNotify()
        // is allowed immediately (even if clock starts at 0). Subtracting an extra
        // 1 ms ensures now - last >= debouncePeriodMs.
        this.lastNotificationTime.set(clock.millis() - debouncePeriodMs - 1);
    }

    /**
     * Backwards-compatible constructor that uses the system clock.
     *
     * @param debouncePeriodMs The minimum time (in milliseconds) that must
     *                         pass between successful notifications.
     */
    public DebounceCondition(long debouncePeriodMs) {
        this(debouncePeriodMs, Clock.systemUTC());
    }

    /**
     * Checks if the debounce period has elapsed and atomically updates
     * the last notification time.
     *
     * @return true if the notification should proceed, false otherwise.
     */
    @Override
    public boolean shouldNotify() {
        long now = clock.millis();
        long last = lastNotificationTime.get();

        if (isTooSoon(now, last)) {
            return false;
        }

        // Atomically set the new time *only if* the 'last' time hasn't changed.
        // If this returns true: We successfully set the time, so we notify.
        // If this returns false: Another thread just set the time, so we are
        //                      the "bounce" event and should be suppressed.
        return lastNotificationTime.compareAndSet(last, now);
    }

    private boolean isTooSoon(long now, long last) {
        return now - last < this.debouncePeriodMs;
    }
}