package pl.catchex.lifecycle;

import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.reminder.TaskReminderService;
import pl.catchex.synchonizer.TaskRepositorySynchronizer;
import pl.catchex.tray.TrayService;

import java.util.concurrent.ScheduledExecutorService;

public class DefaultApplicationStopperFactory implements ApplicationStopperFactory {
    @Override
    public ApplicationStopper create(FileWatcher tasksFileWatcher, TaskRepositorySynchronizer synchronizer, TaskReminderService reminderService, ScheduledExecutorService reminderExecutor, TrayService createdTrayService) {
        return new ApplicationStopper(tasksFileWatcher, synchronizer, reminderService, reminderExecutor, createdTrayService);
    }
}

