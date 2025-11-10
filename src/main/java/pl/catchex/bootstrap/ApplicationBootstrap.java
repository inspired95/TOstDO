package pl.catchex.bootstrap;

import com.google.inject.Inject;
import pl.catchex.ApplicationAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationBootstrap.class);
    private final ApplicationAssembler assembler;

    @Inject
    public ApplicationBootstrap(ApplicationAssembler assembler) {
        this.assembler = assembler;
    }

    /**
     * Starts the application by delegating to ApplicationAssembler.run(). This method blocks
     * until the assembler returns (which happens on shutdown).
     */
    public void run() {
        logger.info("ApplicationBootstrap starting application...");
        assembler.run();
        logger.info("ApplicationBootstrap finished.");
    }

    /**
     * Optional stop method to trigger controlled shutdown programmatically.
     */
    public void stop() {
        try {
            assembler.stop();
        } catch (Exception e) {
            logger.warn("Exception during application stop: {}", e.getMessage());
        }
    }
}
