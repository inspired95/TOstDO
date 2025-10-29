package pl.catchex.reader;

import pl.catchex.config.reader.todoitem.priority.symbol.SymbolConfiguration;
import pl.catchex.model.ToDoItem.Priority;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PriorityParser {

    private static final Logger logger = Logger.getLogger(PriorityParser.class.getName());

    private final Map<String, Priority> priorityMap = new HashMap<>();

    public PriorityParser(SymbolConfiguration configuration)
    {
        priorityMap.put(configuration.getLow(), Priority.LOW);
        priorityMap.put(configuration.getMedium(), Priority.MEDIUM);
        priorityMap.put(configuration.getHigh(), Priority.HIGH);
    }

    public Optional<Priority> parse(String priorityStr){
        Priority p = priorityMap.get(priorityStr);
        if (p == null) {
            logger.log(Level.WARNING, "Unknown priorityStr [ priorityStr={0} ]", priorityStr);
            return Optional.empty();
        }
        return Optional.of(p);
    }
}
