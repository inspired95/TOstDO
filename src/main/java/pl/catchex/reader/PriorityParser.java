package pl.catchex.reader;

import pl.catchex.config.Configuration;
import pl.catchex.model.ToDoItem.Priority;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PriorityParser {
    private final Map<String, Priority> priorityMap = new HashMap<>();

    public PriorityParser(Configuration configuration)
    {
        priorityMap.put(configuration.getLowPriorityTextSymbol(), Priority.LOW);
        priorityMap.put(configuration.getMediumPriorityTextSymbol(), Priority.MEDIUM);
        priorityMap.put(configuration.getHighPriorityTextSymbol(), Priority.HIGH);
    }

    public Optional<Priority> parse(String priority){
        Priority p = priorityMap.get(priority);
        if (p == null) {
            throw new IllegalArgumentException("Unknown priority: " + priority);
        }
        return Optional.ofNullable(p);
    }
}
