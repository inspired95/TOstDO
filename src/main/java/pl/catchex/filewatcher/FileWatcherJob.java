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

        while (true) {
            WatchKey key;
            try {
                key = watcher.watchService.take();
            } catch (InterruptedException e) {
                // Standard way to stop: Thread was interrupted.
                logger.warn("Job interrupted.");
                Thread.currentThread().interrupt(); // Restore the interrupt flag
                break;
            } catch (ClosedWatchServiceException e) {
                // Standard way to stop: service.close() was called.
                logger.warn("WatchService closed, stopping thread.");
                break;
            }

            // Process all events for the key
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
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

            boolean valid = key.reset();
            if (!valid) {
                logger.warn("[Virtual Thread] Watched directory has become inaccessible.");
                break;
            }
        }

        logger.info("Job finished.");
    }
}
