package pl.catchex.filewatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;

public class FileWatcherJob implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FileWatcherJob.class);

    private final FileWatcher watcher;

    FileWatcherJob(FileWatcher watcher){
        this.watcher = watcher;
    }

    @Override
    public void run() {
        logger.info("Job starting...");

        boolean running = true;
        while (running) {
            WatchKey key = takeKeyOrNull();
            if (key == null) {
                running = false; // interrupted or watch service closed
            } else {
                processKey(key);
                running = resetKeyAndLog(key);
            }
        }

        logger.info("Job finished.");
    }

    private WatchKey takeKeyOrNull() {
        try {
            return watcher.watchService.take();
        } catch (InterruptedException e) {
            // Standard way to stop: Thread was interrupted.
            logger.warn("Job interrupted.");
            Thread.currentThread().interrupt(); // Restore the interrupt flag
            return null;
        } catch (ClosedWatchServiceException e) {
            // Standard way to stop: service.close() was called.
            logger.warn("WatchService closed, stopping thread.");
            return null;
        }
    }

    private void processKey(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            handleEvent(event);
        }
    }

    private void handleEvent(WatchEvent<?> event) {
        WatchEvent.Kind<?> kind = event.kind();

        if (kind == StandardWatchEventKinds.OVERFLOW) {
            return;
        }

        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path changedFile = ev.context();

            if (changedFile.equals(watcher.fileName)) {
                watcher.notifyListeners();
            }
        }
    }

    private boolean resetKeyAndLog(WatchKey key) {
        boolean valid = key.reset();
        if (!valid) {
            logger.warn("Watched directory has become inaccessible.");
        }
        return valid;
    }
}
