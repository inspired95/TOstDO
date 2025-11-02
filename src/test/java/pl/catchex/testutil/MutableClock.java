package pl.catchex.testutil;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple mutable Clock for tests. Thread-safe via AtomicLong.
 * Provides convenience constructors and a method to advance time.
 */
public class MutableClock extends Clock {
    private final AtomicLong millis;
    private final ZoneId zone;

    public MutableClock(long initialMillis, ZoneId zone) {
        this.millis = new AtomicLong(initialMillis);
        this.zone = zone;
    }

    public MutableClock(Instant initialInstant, ZoneId zone) {
        this(initialInstant.toEpochMilli(), zone);
    }

    /**
     * Advance clock by given milliseconds.
     */
    public void addMillis(long ms) {
        this.millis.addAndGet(ms);
    }

    @Override
    public Instant instant() {
        return Instant.ofEpochMilli(this.millis.get());
    }

    @Override
    public ZoneId getZone() {
        return this.zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(this.millis.get(), zone);
    }
}

