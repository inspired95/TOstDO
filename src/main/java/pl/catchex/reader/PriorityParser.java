package pl.catchex.reader;

import pl.catchex.config.reader.taskConfiguration.SymbolConfiguration;
import pl.catchex.model.Task.Priority;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriorityParser {

    private static final Logger logger = LoggerFactory.getLogger(PriorityParser.class);

    private final Map<String, Priority> priorityMap = new HashMap<>();

    /**
     * Create a PriorityParser using symbol configuration mapping strings to priorities.
     *
     * @param configuration configuration holding textual symbols for priorities
     */
    public PriorityParser(SymbolConfiguration configuration)
    {
        priorityMap.put(configuration.getLow(), Priority.LOW);
        priorityMap.put(configuration.getMedium(), Priority.MEDIUM);
        priorityMap.put(configuration.getHigh(), Priority.HIGH);
    }

    /**
     * Parse a priority string into an Optional Priority enum.
     *
     * @param priorityStr textual priority symbol
     * @return Optional containing the priority if recognized, otherwise empty
     */
    public Optional<Priority> parse(String priorityStr){
        Priority p = priorityMap.get(priorityStr);
        if (p == null) {
            logger.warn( "Unknown priorityStr [ priorityStr={} ]", priorityStr);
            return Optional.empty();
        }
        return Optional.of(p);
    }
}
