package pl.catchex.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import pl.catchex.ApplicationAssembler;
import pl.catchex.config.AppConfiguration;
import pl.catchex.frequency.TaskFrequencyService;
import pl.catchex.reminder.TaskReminderService;
import pl.catchex.tray.NotificationSender;
import pl.catchex.tray.NotificationSenderFactory;
import pl.catchex.tray.TrayService;
import pl.catchex.lifecycle.ApplicationStopperFactory;
import pl.catchex.lifecycle.DefaultApplicationStopperFactory;
import pl.catchex.bootstrap.FileSystemService;
import pl.catchex.bootstrap.RealFileSystemService;

import java.time.Clock;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class AppModule extends AbstractModule {

    private final AppConfiguration config;
    private final NotificationSenderFactory.Provider trayProvider;

    public AppModule(AppConfiguration config) {
        this(config, NotificationSenderFactory.createNoop());
    }

    /**
     * Constructor that allows providing a custom NotificationSenderFactory.Provider (useful for production to pass a real tray provider).
     */
    public AppModule(AppConfiguration config, NotificationSenderFactory.Provider trayProvider) {
        this.config = config;
        this.trayProvider = trayProvider == null ? NotificationSenderFactory.createNoop() : trayProvider;
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
    public pl.catchex.tray.NotificationSenderFactory.Provider provideTrayProvider() {
        return this.trayProvider;
    }

    @Provides
    @Singleton
    public NotificationSender provideNoopNotificationSender(pl.catchex.tray.NotificationSenderFactory.Provider provider) {
        NotificationSender sender = provider.getNotificationSender();
        if (sender == null) {
            sender = NotificationSenderFactory.createNoop().getNotificationSender();
        }
        return sender;
    }

    @Provides
    @Singleton
    public TaskReminderService provideTaskReminderService(TaskFrequencyService frequencyService, ScheduledExecutorService executor, NotificationSender sender) {
        return new TaskReminderService(frequencyService, executor, sender);
    }

    @Provides
    @Singleton
    public TaskFrequencyService provideTaskFrequencyService() {
        return new TaskFrequencyService(Clock.systemDefaultZone(), config.getConfiguration().getReminderConfiguration());
    }

    @Provides
    @Singleton
    public ApplicationStopperFactory provideApplicationStopperFactory() {
        return new DefaultApplicationStopperFactory();
    }

    @Provides
    @Singleton
    public ApplicationAssembler provideApplicationAssembler(NotificationSender sender, ApplicationStopperFactory stopperFactory, pl.catchex.tray.NotificationSenderFactory.Provider provider) {
        TrayService created = provider.getTrayService();
        return new ApplicationAssembler(config, sender, created, stopperFactory);
    }

    @Provides
    @Singleton
    public pl.catchex.bootstrap.ApplicationBootstrap provideApplicationBootstrap(ApplicationAssembler assembler) {
        return new pl.catchex.bootstrap.ApplicationBootstrap(assembler);
    }

    @Provides
    @Singleton
    public FileSystemService provideFileSystemService() {
        return new RealFileSystemService();
    }

    @Provides
    @Singleton
    public pl.catchex.bootstrap.PathProvider providePathProvider(FileSystemService fs) {
        return new pl.catchex.bootstrap.DefaultPathProvider(fs);
    }

    @Provides
    @Singleton
    public pl.catchex.bootstrap.ConfigCreator provideConfigCreator(FileSystemService fs) {
        return new pl.catchex.bootstrap.DefaultConfigCreatorImpl(fs);
    }
}
