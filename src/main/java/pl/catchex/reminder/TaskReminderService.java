package pl.catchex.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.frequency.TaskFrequencyService;
import pl.catchex.model.IntervalMinutes;
import pl.catchex.model.Task;
import pl.catchex.model.TaskRepositoryListener;
import pl.catchex.tray.NotificationSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages scheduling of reminders for tasks based on repository events
 */
public class TaskReminderService implements TaskRepositoryListener {

    private final TaskFrequencyService frequencyService;
    private final ScheduledExecutorService executorService;
    private final NotificationSender notificationSender; // may be null, then only logging is performed
    private final Map<Task, ScheduledFuture<?>> activeReminders = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TaskReminderService.class);

    /**
     * Creates a new reminder service.
     *
     * @param frequencyService service to calculate reminder intervals
     * @param executorService  executor to run scheduled tasks
     * @param notificationSender optional notification sender (may be null)
     */
    public TaskReminderService(TaskFrequencyService frequencyService, ScheduledExecutorService executorService, NotificationSender notificationSender) {
        this.frequencyService = frequencyService;
        this.executorService = executorService;
        this.notificationSender = notificationSender;
    }

    @Override
    public void onTaskAdded(Task task) {
        // Prevent duplicate scheduling if the task is already being tracked
        if (activeReminders.containsKey(task)) {
            logger.warn("Attempted to add a reminder for an already tracked task: {}", task);
            return;
        }

        IntervalMinutes interval = frequencyService.calculateTaskInterval(task);
        long intervalMinutes = interval.value();

        Runnable reminderTask = () -> {
            logger.info("--- TASK REMINDER (every {} min) ---", intervalMinutes);
            logger.info("{}", task);
            logger.info("-------------------------------------------------");
            // Send a notification via NotificationSender if injected
            try {
                if (notificationSender != null) {
                    String title = "TOstDO - reminder";
                    String message = task.toString();
                    notificationSender.send(title, message, java.awt.TrayIcon.MessageType.INFO);
                } else {
                    logger.debug("NotificationSender is not available - GUI notifications disabled");
                }
            } catch (Exception e) {
                // Do not break reminder scheduling if GUI fails
                logger.warn("Exception while sending notification: {}", e.getMessage());
            }
        };

        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(
                reminderTask,
                intervalMinutes,
                intervalMinutes,
                TimeUnit.MINUTES
        );

        activeReminders.put(task, scheduledFuture);
        logger.info("Started reminder for: {} (every {} min)", task, intervalMinutes);
    }

    @Override
    public void onTaskRemoved(Task task) {
        ScheduledFuture<?> scheduledFuture = activeReminders.remove(task);

        if (scheduledFuture != null) {
            // false: do not interrupt if currently running, but prevent future executions
            scheduledFuture.cancel(false);
            logger.info("Cancelled reminder for: {}", task);
        } else {
            logger.debug("Attempted to remove an untracked reminder: {}", task);
        }
    }

    /**
     * Shuts down the scheduled executor service used by this service.
     */
    public void stop() {
        logger.info("Stopping...");
        executorService.shutdown();
        try {
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
        logger.info("Stopped.");
    }
}