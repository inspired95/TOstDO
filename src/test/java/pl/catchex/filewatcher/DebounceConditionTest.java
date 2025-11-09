package pl.catchex.filewatcher;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import pl.catchex.testutil.MutableClock;

import static org.junit.jupiter.api.Assertions.*;

class DebounceConditionTest {

    @Test
    void firstCallAllowedSubsequentSuppressedUntilTimeout() {
        // given
        MutableClock clock = new MutableClock(Instant.ofEpochMilli(0), ZoneId.of("UTC"));
        DebounceCondition cond = new DebounceCondition(100, clock);

        // when / then
        assertTrue(cond.shouldNotify());

        // when / then
        assertFalse(cond.shouldNotify());

        // when
        clock.addMillis(150);

        // then
        assertTrue(cond.shouldNotify());
    }

    @Test
    void concurrentCallsOnlyOneAllowed() throws InterruptedException {
        // given
        MutableClock clock = new MutableClock(Instant.ofEpochMilli(0), ZoneId.of("UTC"));
        DebounceCondition cond = new DebounceCondition(1000, clock);

        // when: simulate two threads calling
        final boolean[] results = new boolean[2];

        Thread t1 = new Thread(() -> results[0] = cond.shouldNotify());
        Thread t2 = new Thread(() -> results[1] = cond.shouldNotify());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // then
        int successCount = 0;
        if (results[0]) successCount++;
        if (results[1]) successCount++;

        assertEquals(1, successCount, "Only one concurrent caller should be allowed to notify");
    }
}
