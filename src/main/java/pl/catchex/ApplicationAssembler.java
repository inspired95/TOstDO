package pl.catchex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.bootstrap.AppDirectoryInitializer;
import pl.catchex.common.DateParser;
import pl.catchex.config.AppConfiguration;
import pl.catchex.frequency.ToDoFrequencyService;
import pl.catchex.model.ToDoRepository;
import pl.catchex.reader.PriorityParser;
import pl.catchex.reader.ToDoReader;
import pl.catchex.reader.lineparser.ToDoLineParserDispatcher;
import pl.catchex.reminder.ToDoReminderService;
import pl.catchex.synchonizer.ToDoRepositorySynchronizer;
import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.filewatcher.DebounceCondition;
import pl.catchex.tray.NotificationSender;
import pl.catchex.tray.TrayService;
import pl.catchex.lifecycle.ApplicationStopper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Non-static application assembler that builds and runs the application components.
 *
 * <p>This class is responsible for composing the application's main components
 * (parsers, reader, synchronizer, file watcher) and running the application
 * lifecycle (start, wait for shutdown, stop).</p>
 */
public class ApplicationAssembler {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAssembler.class);

    private final CountDownLatch shutdownLatch;
    private final AppConfiguration config;
    private final NotificationSender notificationSender;

    private FileWatcher todoFileWatcher;

    private ToDoRepository repository;
    private ToDoRepositorySynchronizer synchronizer;

    private ToDoReminderService reminderService;
    private ScheduledExecutorService reminderExecutor;
    private final TrayService createdTrayService;
    private final pl.catchex.lifecycle.ApplicationStopperFactory applicationStopperFactory;

    /**
     * Create a new ApplicationAssembler using the provided configuration.
     *
     * @param config application configuration used to build components
     */
    public ApplicationAssembler(AppConfiguration config, NotificationSender notificationSender, TrayService createdTrayService) {
        this(config, notificationSender, createdTrayService, new pl.catchex.lifecycle.DefaultApplicationStopperFactory());
    }

    /**
     * Constructor that accepts an ApplicationStopperFactory to allow tests to
     * inject a custom factory producing mock or instrumented stoppers.
     */
    public ApplicationAssembler(AppConfiguration config, NotificationSender notificationSender, TrayService createdTrayService, pl.catchex.lifecycle.ApplicationStopperFactory applicationStopperFactory) {
        this.config = config;
        this.notificationSender = notificationSender;
        this.createdTrayService = createdTrayService;
        this.shutdownLatch = new CountDownLatch(1);
        this.applicationStopperFactory = applicationStopperFactory;
    }

    /**
     * Build application components and run the main lifecycle loop.
     *
     * <p>This method performs an initial synchronization, starts a file watcher
     * and then blocks until a shutdown signal is received.</p>
     */
    public void run() {
        logger.info("TOstDO application starting (assembled)...");

        // Ensure user application directory and default files exist
        AppDirectoryInitializer.initializeSafely();

        registerShutdownHook();

        try {
            ToDoLineParserDispatcher dispatcher = createDispatcher();
            Path todoFile = Paths.get(config.getConfiguration().getToDoFilePath());

            ToDoReader todoReader = createToDoReader(dispatcher, todoFile);
            synchronizer = createSynchronizer(todoReader);

            this.reminderExecutor = createReminderExecutor();
            this.reminderService = createReminderService(this.reminderExecutor, this.notificationSender);
            this.repository.addListener(this.reminderService);
            logger.info("ToDoReminderService registered with repository.");
            synchronizer.synchronizeRepository();

            startWatcher(todoFile, synchronizer);

            awaitShutdown();

        } catch (IOException e) {
            logger.error("I/O exception [ message={}]", e.getMessage());
        } finally {
            logger.info("All task finished -> shutdown main thread");
            logger.info("TOstDO application completely finished");
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received -> TOstDO application closing procedure initiation");
            safeStop();
        }));
    }

    private void safeStop() {
        try {
            stop();
        } catch (IOException e) {
            logger.error("I/O exception while stopping application [ message={}]", e.getMessage());
        }
    }

    private ToDoLineParserDispatcher createDispatcher() {
        PriorityParser priorityParser = new PriorityParser(
                config.getConfiguration().getTodoItem().getPriority().getSymbol()
        );
        DateParser dateParser = new DateParser(
                config.getConfiguration().getTodoItem().getDateFormat()
        );
        return new ToDoLineParserDispatcher(priorityParser, dateParser);
    }

    private ToDoReader createToDoReader(ToDoLineParserDispatcher dispatcher, Path todoFile) {
        return new ToDoReader(dispatcher, todoFile);
    }

    private ToDoRepositorySynchronizer createSynchronizer(ToDoReader todoReader) {
        this.repository = new ToDoRepository();
        this.synchronizer = new ToDoRepositorySynchronizer(todoReader, this.repository);
        return this.synchronizer;
    }

    private ScheduledExecutorService createReminderExecutor() {
        ThreadFactory factory = runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            t.setName("tostdo-reminder-executor");
            return t;
        };
        return Executors.newSingleThreadScheduledExecutor(factory);
    }

    private ToDoReminderService createReminderService(ScheduledExecutorService executor, NotificationSender notificationSender) {
        ToDoFrequencyService frequencyService = new ToDoFrequencyService(
                Clock.systemDefaultZone(),
                config.getConfiguration().getReminderConfiguration()
        );
        return new ToDoReminderService(frequencyService, executor, notificationSender);
    }

    private void startWatcher(Path todoFile, ToDoRepositorySynchronizer synchronizer) throws IOException {
        this.todoFileWatcher = new FileWatcher(todoFile, new DebounceCondition(250));
        this.todoFileWatcher.addListener(synchronizer);
        this.todoFileWatcher.start();
    }

    private void awaitShutdown() {
        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted [ message={}]", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stop the running application: stop the file watcher (if started) and
     * release the internal shutdown latch so the main thread can exit.
     *
     * @throws IOException when closing the file watcher fails
     */
    public void stop() throws IOException {
        // create an ApplicationStopper via the factory and stop components
        pl.catchex.lifecycle.ApplicationStopper stopper = this.applicationStopperFactory.create(
                this.todoFileWatcher,
                this.synchronizer,
                this.reminderService,
                this.reminderExecutor,
                this.createdTrayService
        );

        stopper.stop();

        // clear references to allow GC and signal shutdown
        this.synchronizer = null;
        this.reminderService = null;
        this.repository = null;

        shutdownLatch.countDown();
    }
}
