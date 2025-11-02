package pl.catchex.filewatcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebounceConditionTest {

    @Test
    void firstCallAllowedSubsequentSuppressedUntilTimeout() throws InterruptedException {
        DebounceCondition cond = new DebounceCondition(100);

        // first call should be allowed
        assertTrue(cond.shouldNotify());

        // immediate second call should be suppressed
        assertFalse(cond.shouldNotify());

        // after waiting longer than debounce period, it should allow again
        Thread.sleep(150);
        assertTrue(cond.shouldNotify());
    }

    @Test
    void concurrentCallsOnlyOneAllowed() throws InterruptedException {
        DebounceCondition cond = new DebounceCondition(1000);

        // simulate two threads calling at the same time; only one should succeed
        final boolean[] results = new boolean[2];

        Thread t1 = new Thread(() -> results[0] = cond.shouldNotify());
        Thread t2 = new Thread(() -> results[1] = cond.shouldNotify());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        int successCount = 0;
        if (results[0]) successCount++;
        if (results[1]) successCount++;

        assertEquals(1, successCount, "Only one concurrent caller should be allowed to notify");
    }
}

