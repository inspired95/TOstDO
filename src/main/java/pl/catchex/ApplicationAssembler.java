package pl.catchex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.catchex.common.DateParser;
import pl.catchex.config.AppConfiguration;
import pl.catchex.model.ToDoRepository;
import pl.catchex.reader.PriorityParser;
import pl.catchex.reader.ToDoReader;
import pl.catchex.reader.lineparser.ToDoLineParserDispatcher;
import pl.catchex.synchonizer.ToDoRepositorySynchronizer;
import pl.catchex.filewatcher.FileWatcher;
import pl.catchex.filewatcher.DebounceCondition;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

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

        // Optionally, clear repository reference to help GC
        this.synchronizer = null;
        this.repository = null;

        shutdownLatch.countDown();
    }
}
