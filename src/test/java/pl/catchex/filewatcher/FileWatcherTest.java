package pl.catchex.filewatcher;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.catchex.testutil.MutableClock;

import static org.junit.jupiter.api.Assertions.*;

class FileWatcherTest {

    @Test
    void notifyListeners_respectsDebounceAndCallsListeners() throws Exception {
        // given
        Path tmpFile = Files.createTempFile("fwtest", ".txt");
        try {
            MutableClock testClock = new MutableClock(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("UTC"));
            DebounceCondition condition = new DebounceCondition(50, testClock);

            FileWatcher watcher = new FileWatcher(tmpFile, condition);

            AtomicBoolean called = new AtomicBoolean(false);
            FileChangeListener listener = path -> called.set(true);
            watcher.addListener(listener);

            // when
            watcher.notifyListeners();

            // then
            assertTrue(called.get());

            // when
            called.set(false);
            watcher.notifyListeners();

            // then
            assertFalse(called.get());

            // when
            testClock.addMillis(60);
            watcher.notifyListeners();

            // then
            assertTrue(called.get());
        } finally {
            Files.deleteIfExists(tmpFile);
        }
    }

    @Test
    void addAndRemoveListenerLogging() throws Exception {
        // given
        Path tmpFile = Files.createTempFile("fwtest2", ".txt");
        try {
            MutableClock testClock = new MutableClock(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("UTC"));
            DebounceCondition condition = new DebounceCondition(50, testClock);

            FileWatcher watcher = new FileWatcher(tmpFile, condition);
            FileChangeListener listener = path -> {};

            // when / then
            assertDoesNotThrow(() -> {
                watcher.addListener(listener);
                watcher.removeListener(listener);
            });
        } finally {
            Files.deleteIfExists(tmpFile);
        }
    }
}
