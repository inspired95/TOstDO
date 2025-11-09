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

    private final Runnable job;
    final Path fileName;
    private final NotificationCondition notificationCondition;
    private Thread workerThread;

    /**
     * Create a new FileWatcher that will monitor the provided file and
     * invoke registered {@link FileChangeListener}s when changes are detected.
     *
     * @param fileToWatch           file to monitor
     * @param notificationCondition strategy that decides whether an event should notify listeners
     * @throws IOException              when WatchService registration fails
     * @throws IllegalArgumentException when the provided path is not valid for watching
     */
    public FileWatcher(Path fileToWatch, NotificationCondition notificationCondition) throws IOException, IllegalArgumentException {
        FileValidationResult validationResult = FileToWatchValidator.validate(fileToWatch);
        if (validationResult.failed()) {
            throw new IllegalArgumentException("Cannot create FileWatcher: " + validationResult.getMessage());
        }
        Path dirToWatch = fileToWatch.getParent();
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.fileName = fileToWatch.getFileName();
            dirToWatch.register(
                    this.watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );
        } catch (IOException e) {
            throw new IOException("I/O error while registering WatchService for " + dirToWatch, e);
        }
        this.job = new FileWatcherJob(this);

        this.notificationCondition = notificationCondition;
    }

    /**
     * Start the file-watching job in a new virtual thread.
     */
    public void start() {
        logger.info("Starting monitoring job in a new virtual thread...");
        this.workerThread = Thread.ofVirtual().start(this.job);
    }

    /**
     * Stop watching the file and shut down the internal WatchService.
     *
     * @throws IOException when closing the watch service fails
     */
    public void stop() throws IOException {
        logger.info("Stopping file watcher...");

        // This will cause the watchService.take() to throw ClosedWatchServiceException
        if (watchService != null) {
            watchService.close();
        }

        // This is the standard "backup" way to stop a thread
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    /**
     * Register a listener to be notified when the watched file changes.
     *
     * @param listener listener to register
     */
    public void addListener(FileChangeListener listener) {
        listeners.add(listener);
        logger.info("Added listener [ listener={} ] ", listener.getClass().getSimpleName());
    }

    /**
     * Unregister a previously registered listener.
     *
     * @param listener listener to unregister
     */
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
