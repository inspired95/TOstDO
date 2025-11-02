package pl.catchex.filewatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.nio.file.*;
public class FileWatcher {
    private static final Logger logger = LoggerFactory.getLogger(FileWatcher.class);

    private final List<FileChangeListener> listeners = new CopyOnWriteArrayList<>();

    final WatchService watchService;

    private final Path dirToWatch;

    private final Runnable job;
    final Path fileName;
    private final NotificationCondition notificationCondition;
    private Thread workerThread;

    public FileWatcher(Path fileToWatch) throws IOException, IllegalArgumentException {
        this(fileToWatch, 250);
    }

    public FileWatcher(Path fileToWatch, long debouncePeriodMs) throws IOException, IllegalArgumentException {
        FileValidationResult validationResult = FileToWatchValidator.validate(fileToWatch);
        if (validationResult.failed()) {
            throw new IllegalArgumentException("Cannot create FileWatcher: " + validationResult.getMessage());
        }
        this.dirToWatch = fileToWatch.getParent();
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.fileName = fileToWatch.getFileName();
            this.dirToWatch.register(
                    this.watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );
        } catch (IOException e) {
            throw new IOException("I/O error while registering WatchService for " + dirToWatch, e);
        }
        this.job = new FileWatcherJob(this);

        this.notificationCondition = new DebounceCondition(debouncePeriodMs);
    }

    public Thread start() {
        logger.info("Starting monitoring job in a new virtual thread...");
        this.workerThread = Thread.ofVirtual().start(this.job);
        return this.workerThread;
    }

    public void stop() throws IOException {
        logger.info("Stopping watcher...");

        // This will cause the watchService.take() to throw ClosedWatchServiceException
        if (watchService != null) {
            watchService.close();
        }

        // This is the standard "backup" way to stop a thread
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    public void addListener(FileChangeListener listener) {
        listeners.add(listener);
        logger.info("Added listener [ listener={} ] ", listener.getClass().getSimpleName());
    }

    public void removeListener(FileChangeListener listener) {
        listeners.remove(listener);
        logger.info("Removed listener [ listener={} ] ", listener.getClass().getSimpleName());
    }

    void notifyListeners() {
        if (!notificationCondition.shouldNotify()) {
            logger.debug("Debouncing event (suppressed)");
            return;
        }

        logger.info(" Change detected! Notifying {} listener(s)...", listeners.size());
        for (FileChangeListener listener : listeners) {
            try {
                listener.onFileModified(null);
            } catch (Exception e) {
                logger.warn("Error notifying listener [ listener={} ]" ,listener.getClass().getSimpleName());
            }
        }
    }
}
