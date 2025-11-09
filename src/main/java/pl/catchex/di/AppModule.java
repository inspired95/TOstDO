package pl.catchex.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import pl.catchex.ApplicationAssembler;
import pl.catchex.config.AppConfiguration;
import pl.catchex.frequency.ToDoFrequencyService;
import pl.catchex.reminder.ToDoReminderService;
import pl.catchex.tray.NotificationSender;
import pl.catchex.tray.NotificationSenderFactory;
import pl.catchex.tray.TrayService;
import pl.catchex.lifecycle.ApplicationStopperFactory;
import pl.catchex.lifecycle.DefaultApplicationStopperFactory;

import java.time.Clock;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class AppModule extends AbstractModule {

    private final AppConfiguration config;
    private final NotificationSenderFactory.Provider trayProvider;

    public AppModule(AppConfiguration config) {
        this.config = config;
        this.trayProvider = NotificationSenderFactory.createDefaultTrayService();
    }

    @Override
    protected void configure() {
        // default bindings (if needed) can go here
    }

    @Provides
    @Singleton
    public ScheduledExecutorService provideReminderExecutor() {
        ThreadFactory factory = runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            t.setName("tostdo-reminder-executor");
            return t;
        };
        return new ScheduledThreadPoolExecutor(1, factory);
    }

    @Provides
    @Singleton
    public NotificationSender provideNoopNotificationSender() {
        NotificationSender sender = trayProvider.getNotificationSender();
        if (sender == null) {
            sender = NotificationSenderFactory.createNoop().getNotificationSender();
        }
        return sender;
    }

    @Provides
    @Singleton
    public ToDoReminderService provideToDoReminderService(ToDoFrequencyService frequencyService, ScheduledExecutorService executor, NotificationSender sender) {
        return new ToDoReminderService(frequencyService, executor, sender);
    }

    @Provides
    @Singleton
    public ToDoFrequencyService provideToDoFrequencyService() {
        return new ToDoFrequencyService(Clock.systemDefaultZone(), config.getConfiguration().getReminderConfiguration());
    }

    @Provides
    @Singleton
    public ApplicationStopperFactory provideApplicationStopperFactory() {
        return new DefaultApplicationStopperFactory();
    }

    @Provides
    @Singleton
    public ApplicationAssembler provideApplicationAssembler(NotificationSender sender, ApplicationStopperFactory stopperFactory) {
        TrayService created = trayProvider.getTrayService();
        return new ApplicationAssembler(config, sender, created, stopperFactory);
    }

    @Provides
    @Singleton
    public pl.catchex.bootstrap.ApplicationBootstrap provideApplicationBootstrap(ApplicationAssembler assembler) {
        return new pl.catchex.bootstrap.ApplicationBootstrap(assembler);
    }
}
