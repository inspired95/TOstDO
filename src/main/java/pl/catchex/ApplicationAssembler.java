package pl.catchex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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

    private FileWatcher todoFileWatcher;

    // Keep references so we can unregister listeners during shutdown
    private ToDoRepository repository;
    private ToDoRepositorySynchronizer synchronizer;

    private ToDoReminderService reminderService; // <-- NOWE POLE
    private ScheduledExecutorService reminderExecutor; // <-- NOWE POLE

    /**
     * Create a new ApplicationAssembler using the provided configuration.
     *
     * @param config application configuration used to build components
     */
    public ApplicationAssembler(AppConfiguration config) {
        this.config = config;
        this.shutdownLatch = new CountDownLatch(1);
    }

    /**
     * Build application components and run the main lifecycle loop.
     *
     * <p>This method performs an initial synchronization, starts a file watcher
     * and then blocks until a shutdown signal is received.</p>
     */
    public void run() {
        logger.info("TOstDO application starting (assembled)...");

        registerShutdownHook();

        try {
            ToDoLineParserDispatcher dispatcher = createDispatcher();
            Path todoFile = Paths.get(config.getConfiguration().getToDoFilePath());

            ToDoReader todoReader = createToDoReader(dispatcher, todoFile);
            synchronizer = createSynchronizer(todoReader);

            this.reminderExecutor = createReminderExecutor();
            this.reminderService = createReminderService(this.reminderExecutor);
            this.repository.addListener(this.reminderService); // Rejestrujemy listenera
            logger.info("ToDoReminderService zarejestrowany w repozytorium.");
            // initial sync to populate repository
            synchronizer.synchronizeRepository();

            // start watching file changes
            startWatcher(todoFile, synchronizer);

            // block until shutdown
            awaitShutdown();

        } catch (IOException e) {
            logger.error("I/O exception [ message={} ]", e.getMessage());
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

    // Calls public stop() and handles IOException internally for shutdown hook usage
    private void safeStop() {
        try {
            stop();
        } catch (IOException e) {
            logger.error("I/O exception while stopping application [ message={} ]", e.getMessage());
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
        // store repository and synchronizer references so we can clean them up on stop()
        this.repository = new ToDoRepository();
        this.synchronizer = new ToDoRepositorySynchronizer(todoReader, this.repository);
        return this.synchronizer;
    }

    private ScheduledExecutorService createReminderExecutor() {
        // Używamy prostego ThreadFactory, aby uniknąć zależności od wersji JDK/Project Loom
        ThreadFactory factory = runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            t.setName("tostdo-reminder-executor");
            return t;
        };
        return Executors.newSingleThreadScheduledExecutor(factory);
    }

    private ToDoReminderService createReminderService(ScheduledExecutorService executor) {
        ToDoFrequencyService frequencyService = new ToDoFrequencyService(
                Clock.systemDefaultZone(),
                config.getConfiguration().getReminderConfiguration()
        );
        // Przekazujemy dedykowany logger do serwisu
        return new ToDoReminderService(frequencyService, executor);
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
            logger.error("Main thread interrupted [ message={} ]", e.getMessage());
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
        // Unregister the synchronizer from the watcher to avoid callbacks during shutdown
        if (this.todoFileWatcher != null && this.synchronizer != null) {
            try {
                this.todoFileWatcher.removeListener(this.synchronizer);
            } catch (Exception ex) {
                logger.debug("Failed to remove watcher listener: {}", ex.getMessage());
            }
        }

        if (this.todoFileWatcher != null) {
            this.todoFileWatcher.stop();
        }

        // Zatrzymaj serwisy, które konsumują zdarzenia
        if (this.reminderService != null) {
            this.reminderService.stop(); // Zatrzymaj nasz nowy serwis
        }

        // Bezpieczne zatrzymanie executor'a przypisanego do przypomnień
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

        // Optionally, clear repository reference to help GC
        this.synchronizer = null;
        this.reminderService = null; // <-- NOWA LINIA
        this.repository = null;

        shutdownLatch.countDown();
    }
}
