package pl.catchex.lifecycle;

import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.reminder.ToDoReminderService;
import pl.catchex.synchonizer.ToDoRepositorySynchronizer;
import pl.catchex.tray.TrayService;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Factory for creating {@link ApplicationStopper} instances. Allows tests
 * to inject a mock factory or a custom implementation when needed.
 */
public interface ApplicationStopperFactory {
    ApplicationStopper create(FileWatcher todoFileWatcher,
                               ToDoRepositorySynchronizer synchronizer,
                               ToDoReminderService reminderService,
                               ScheduledExecutorService reminderExecutor,
                               TrayService createdTrayService);
}

