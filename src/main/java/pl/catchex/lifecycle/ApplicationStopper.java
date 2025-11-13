package pl.catchex.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.reminder.TaskReminderService;
import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.synchonizer.TaskRepositorySynchronizer;
import pl.catchex.tray.TrayService;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Helper responsible for stopping application components.
 */
public class ApplicationStopper {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStopper.class);

    private final FileWatcher tasksFileWatcher;
    private final TaskRepositorySynchronizer synchronizer;
    private final TaskReminderService reminderService;
    private final ScheduledExecutorService reminderExecutor;
    private final TrayService createdTrayService;

    public ApplicationStopper(FileWatcher tasksFileWatcher,
                              TaskRepositorySynchronizer synchronizer,
                              TaskReminderService reminderService,
                              ScheduledExecutorService reminderExecutor,
                              TrayService createdTrayService) {
        this.tasksFileWatcher = tasksFileWatcher;
        this.synchronizer = synchronizer;
        this.reminderService = reminderService;
        this.reminderExecutor = reminderExecutor;
        this.createdTrayService = createdTrayService;
    }

    /**
     * Stop provided components. This method attempts to stop everything
     * gracefully and logs exceptions instead of throwing them, except
     * for IO errors coming from FileWatcher.stop() which are propagated.
     */
    public void stop() throws IOException {
        removeSynchronizerListener();
        stopWatcher();
        stopReminderService();
        stopReminderExecutor();
        stopTrayService();
    }

    private void removeSynchronizerListener() {
        if (this.tasksFileWatcher != null && this.synchronizer != null) {
            try {
                this.tasksFileWatcher.removeListener(this.synchronizer);
            } catch (Exception ex) {
                logger.debug("Failed to remove watcher listener: {}", ex.getMessage());
            }
        }
    }

    private void stopWatcher() throws IOException {
        if (this.tasksFileWatcher != null) {
            // FileWatcher.stop() may throw IOException which we propagate
            this.tasksFileWatcher.stop();
        }
    }

    private void stopReminderService() {
        if (this.reminderService != null) {
            try {
                this.reminderService.stop();
            } catch (Exception ex) {
                logger.debug("Exception while stopping reminderService: {}", ex.getMessage());
            }
        }
    }

    private void stopReminderExecutor() {
        if (this.reminderExecutor != null) {
            try {
                this.reminderExecutor.shutdown();
                if (!this.reminderExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    this.reminderExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.debug("Interrupted while stopping reminder executor: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void stopTrayService() {
        if (this.createdTrayService != null) {
            try {
                this.createdTrayService.stop();
            } catch (Exception t) {
                logger.debug("Exception while closing createdTrayService: {}", t.getMessage());
            }
        }
    }
}

