package pl.catchex.lifecycle;

import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.reminder.ToDoReminderService;
import pl.catchex.synchonizer.ToDoRepositorySynchronizer;
import pl.catchex.tray.TrayService;

import java.util.concurrent.ScheduledExecutorService;

public class DefaultApplicationStopperFactory implements ApplicationStopperFactory {
    @Override
    public ApplicationStopper create(FileWatcher todoFileWatcher, ToDoRepositorySynchronizer synchronizer, ToDoReminderService reminderService, ScheduledExecutorService reminderExecutor, TrayService createdTrayService) {
        return new ApplicationStopper(todoFileWatcher, synchronizer, reminderService, reminderExecutor, createdTrayService);
    }
}

