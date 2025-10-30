package pl.catchex.frequency;

import pl.catchex.model.BaseInterval;
import pl.catchex.model.ToDoItem;

public class ToDoPriorityToBaseIntervalConverter {
    private ToDoPriorityToBaseIntervalConverter() {
    }

    public static BaseInterval convert(ToDoItem.Priority priority){
        return switch (priority){
            case LOW -> BaseInterval.LOW_PRIORITY_BASE_INTERVAL;
            case MEDIUM -> BaseInterval.MEDIUM_PRIORITY_BASE_INTERVAL;
            case HIGH -> BaseInterval.HIGH_PRIORITY_BASE_INTERVAL;
        };
    }
}
