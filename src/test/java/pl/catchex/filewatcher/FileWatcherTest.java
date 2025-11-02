package pl.catchex.filewatcher;

import org.junit.jupiter.api.Test;
import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.filewatcher.FileChangeListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FileWatcherTest {

    @Test
    void notifyListeners_respectsDebounceAndCallsListeners() throws Exception {
        Path tmpFile = Files.createTempFile("fwtest", ".txt");
        try {
            FileWatcher watcher = new FileWatcher(tmpFile, 50);

            AtomicBoolean called = new AtomicBoolean(false);
            FileChangeListener listener = path -> called.set(true);
            watcher.addListener(listener);

            // first notification should pass through
            watcher.notifyListeners();
            assertTrue(called.get());

            called.set(false);
            // immediate next notification should be debounced
            watcher.notifyListeners();
            assertFalse(called.get());

            // after waiting the debounce period it should call again
            Thread.sleep(60);
            watcher.notifyListeners();
            assertTrue(called.get());
        } finally {
            Files.deleteIfExists(tmpFile);
        }
    }

    @Test
    void addAndRemoveListenerLogging() throws Exception {
        Path tmpFile = Files.createTempFile("fwtest2", ".txt");
        try {
            FileWatcher watcher = new FileWatcher(tmpFile, 10);
            FileChangeListener listener = path -> {};
            watcher.addListener(listener);
            watcher.removeListener(listener);
            // no assertions besides not throwing; logging is not asserted here
        } finally {
            Files.deleteIfExists(tmpFile);
        }
    }
}

