package pl.catchex.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.frequency.ToDoFrequencyService;
import pl.catchex.model.ToDoIntervalMinutes;
import pl.catchex.model.ToDoItem;
import pl.catchex.model.ToDoRepositoryListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages scheduling of reminders for ToDo items based on repository events. //NOSONAR
 * Implements ToDoRepositoryListener to react to changes.
 */
public class ToDoReminderService implements ToDoRepositoryListener {

    private final ToDoFrequencyService frequencyService;
    private final ScheduledExecutorService executorService;
    private final Map<ToDoItem, ScheduledFuture<?>> activeReminders = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ToDoReminderService.class);

    /**
     * Creates a new reminder service.
     *
     * @param frequencyService service to calculate reminder intervals
     * @param executorService  executor to run scheduled tasks
     */
    public ToDoReminderService(ToDoFrequencyService frequencyService, ScheduledExecutorService executorService) {
        this.frequencyService = frequencyService;
        this.executorService = executorService;
    }

    @Override
    public void onToDoAdded(ToDoItem item) {
        // Prevent duplicate scheduling if the item is already being tracked
        if (activeReminders.containsKey(item)) {
            logger.warn("Attempted to add a reminder for an already tracked item: {}", item);
            return;
        }

        ToDoIntervalMinutes interval = frequencyService.calculateToDoInterval(item);
        long intervalMinutes = interval.value();

        Runnable reminderTask = () -> {
            logger.info("--- TODO REMINDER (every {} min) ---", intervalMinutes);
            logger.info("{}", item);
            logger.info("-------------------------------------------------");
        };

        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(
                reminderTask,
                intervalMinutes, // initial delay
                intervalMinutes, // period
                TimeUnit.MINUTES
        );

        activeReminders.put(item, scheduledFuture);
        logger.info("Started reminder for: {} (every {} min)", item, intervalMinutes);
    }

    @Override
    public void onToDoRemoved(ToDoItem item) {
        ScheduledFuture<?> scheduledFuture = activeReminders.remove(item);

        if (scheduledFuture != null) {
            // false: do not interrupt if currently running, but prevent future executions
            scheduledFuture.cancel(false);
            logger.info("Cancelled reminder for: {}", item);
        } else {
            logger.debug("Attempted to remove an untracked reminder: {}", item);
        }
    }

    /**
     * Shuts down the scheduled executor service used by this service.
     */
    public void stop() {
        logger.info("Stopping ToDoReminderService...");
        executorService.shutdown();
        try {
            // Wait for currently running tasks to finish
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Executor service did not terminate cleanly, forcing shutdown...");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for executor termination, forcing shutdown.", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        activeReminders.clear();
        logger.info("ToDoReminderService stopped.");
    }
}